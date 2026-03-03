package br.com.novalearn.platform.api.controllers.review;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.review.*;
import br.com.novalearn.platform.domain.enums.ReviewStatus;
import br.com.novalearn.platform.domain.services.review.MyReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.dtos.review.ReviewTestFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MyReviewController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class MyReviewControllerTest extends BaseControllerTest {
    @MockitoBean
    private MyReviewService myReviewService;

    @BeforeEach
    void setUp() { mock_authenticated_user(); }

    @Test
    void should_return_my_reviews_when_list_my_reviews() throws Exception {
        List<ReviewListResponseDTO> response = List.of(
                new ReviewListResponseDTO(
                        8L,
                        1L,
                        5L,
                        5,
                        false,
                        ReviewStatus.APPROVED,
                        true,
                        false
                ),
                new ReviewListResponseDTO(
                        13L,
                        1L,
                        5L,
                        5,
                        false,
                        ReviewStatus.REJECTED,
                        true,
                        false
                )
        );

        when(myReviewService.listMyReviews(USER_ID)).thenReturn(response);

        mockMvc.perform(get("/api/me/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(8L))
                .andExpect(jsonPath("$[1].id").value(13L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].status").value("REJECTED"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].active").value(true));

        verify(myReviewService).listMyReviews(USER_ID);
    }

    @Test
    void should_return_my_review_when_valid_id() throws Exception {
        ReviewResponseDTO response = reviewResponse();

        when(myReviewService.findMyReviewById(8L, USER_ID))
                .thenReturn(response);

        mockMvc.perform(get("/api/me/reviews/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.courseId").value(1L))
                .andExpect(jsonPath("$.comment").value("Some comment"))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(myReviewService).findMyReviewById(8L, USER_ID);
    }

    @Test
    void should_update_my_review_when_valid_request() throws Exception {
        ReviewUpdateRequestDTO request = reviewUpdateRequest();

        ReviewResponseDTO response = new ReviewResponseDTO(
                8L,
                true,
                false,
                LocalDateTime.now().minusDays(3),
                null,
                "New observations",
                1L,
                5L,
                4,
                "New comment",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(2),
                false,
                ReviewStatus.APPROVED,
                5L,
                null
        );

        when(myReviewService.updateMyReview(eq(8L), any(ReviewUpdateRequestDTO.class), eq(USER_ID)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/me/reviews/8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.comment").value("New comment"))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.observations").value("New observations"));

        verify(myReviewService)
                .updateMyReview(eq(8L), any(ReviewUpdateRequestDTO.class), eq(USER_ID));
    }

    @Test
    void should_return_bad_request_when_update_with_invalid_data() throws Exception {
        ReviewUpdateRequestDTO request = new ReviewUpdateRequestDTO(
                0,
                "",
                null,
                ""
        );

        mockMvc.perform(patch("/api/me/reviews/8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_delete_my_review_when_valid_id() throws Exception {
        mockMvc.perform(delete("/api/me/reviews/8")).andExpect(status().isNoContent());
        verify(myReviewService).deleteMyReview(8L, USER_ID);
    }

    @Test
    void should_restore_my_review_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/me/reviews/8/restore")).andExpect(status().isNoContent());
        verify(myReviewService).restoreMyReview(8L, USER_ID);
    }

    @Test
    void should_return_my_review_when_get_by_course() throws Exception {
        ReviewResponseDTO response = reviewResponse();

        when(myReviewService.findMyReviewByCourse(1L, USER_ID))
                .thenReturn(response);

        mockMvc.perform(get("/api/me/reviews/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.courseId").value(1L))
                .andExpect(jsonPath("$.comment").value("Some comment"))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(myReviewService).findMyReviewByCourse(1L, USER_ID);
    }

    @Test
    void should_mark_review_as_anonymous_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/me/reviews/8/mark-anonymous"))
                .andExpect(status().isNoContent());

        verify(myReviewService).markAnonymous(8L, USER_ID);
    }
}