package br.com.novalearn.platform.api.controllers.review;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.review.*;
import br.com.novalearn.platform.domain.enums.ReviewStatus;
import br.com.novalearn.platform.domain.services.review.ReviewService;
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

@WebMvcTest(ReviewController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class ReviewControllerTest extends BaseControllerTest {
    @MockitoBean
    private ReviewService reviewService;

    @BeforeEach
    void setUp() { mock_authenticated_user(); }

    @Test
    void should_return_review_list_when_list_all_active() throws Exception {
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

        when(reviewService.listAllActive()).thenReturn(response);

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(8L))
                .andExpect(jsonPath("$[1].id").value(13L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].status").value("REJECTED"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].active").value(true));

        verify(reviewService).listAllActive();
    }

    @Test
    void should_return_review_when_find_by_id() throws Exception {
        ReviewResponseDTO response = reviewResponse();

        when(reviewService.findById(8L)).thenReturn(response);

        mockMvc.perform(get("/api/reviews/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.courseId").value(1L))
                .andExpect(jsonPath("$.comment").value("Some comment"))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(reviewService).findById(8L);
    }

    @Test
    void should_create_review_when_valid_request() throws Exception {
        ReviewCreateRequestDTO request = reviewCreateRequest();
        ReviewResponseDTO response = reviewResponse();

        when(reviewService.create(eq(USER_ID), any(ReviewCreateRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.courseId").value(1L))
                .andExpect(jsonPath("$.comment").value("Some comment"))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(reviewService).create(eq(USER_ID), any(ReviewCreateRequestDTO.class));
    }

    @Test
    void should_return_bad_request_when_create_with_invalid_data() throws Exception {
        ReviewCreateRequestDTO request = new ReviewCreateRequestDTO(
                null,
                0,
                "",
                null,
                ""
        );

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_update_review_when_valid_request() throws Exception {
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

        when(reviewService.update(eq(8L), any(ReviewUpdateRequestDTO.class), eq(USER_ID)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/reviews/8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.comment").value("New comment"))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.observations").value("New observations"));

        verify(reviewService).update(
                eq(8L), any(ReviewUpdateRequestDTO.class), eq(USER_ID)
        );
    }

    @Test
    void should_activate_review_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/reviews/8/activate"))
                .andExpect(status().isNoContent());

        verify(reviewService).activate(8L, USER_ID);
    }

    @Test
    void should_deactivate_review_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/reviews/8/deactivate"))
                .andExpect(status().isNoContent());

        verify(reviewService).deactivate(8L, USER_ID);
    }

    @Test
    void should_delete_review_when_valid_id() throws Exception {
        mockMvc.perform(delete("/api/reviews/8"))
                .andExpect(status().isNoContent());

        verify(reviewService).delete(8L, USER_ID);
    }

    @Test
    void should_restore_review_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/reviews/8/restore"))
                .andExpect(status().isNoContent());

        verify(reviewService).restore(8L, USER_ID);
    }

    @Test
    void should_return_reviews_when_list_by_course() throws Exception {
        List<ReviewResponseDTO> response = List.of(
                reviewResponse(),
                new ReviewResponseDTO(
                        14L,
                        true,
                        false,
                        LocalDateTime.now().minusDays(3),
                        null,
                        "Other observations",
                        1L,
                        5L,
                        5,
                        "Other comment",
                        LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusHours(3),
                        false,
                        ReviewStatus.REJECTED,
                        5L,
                        null
                )
        );

        when(reviewService.listByCourse(6L)).thenReturn(response);

        mockMvc.perform(get("/api/reviews/course/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(8L))
                .andExpect(jsonPath("$[1].id").value(14L))
                .andExpect(jsonPath("$[0].comment").value("Some comment"))
                .andExpect(jsonPath("$[1].comment").value("Other comment"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].status").value("REJECTED"));

        verify(reviewService).listByCourse(6L);
    }

    @Test
    void should_approve_review_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/reviews/8/approve"))
                .andExpect(status().isNoContent());

        verify(reviewService).approve(8L, USER_ID);
    }

    @Test
    void should_reject_review_when_valid_id_with_observation() throws Exception {
        mockMvc.perform(patch("/api/reviews/8/reject")
                        .param("observation", "Inappropriate content"))
                .andExpect(status().isNoContent());

        verify(reviewService).reject(8L, "Inappropriate content", USER_ID);
    }

    @Test
    void should_reject_review_when_valid_id_without_observation() throws Exception {
        mockMvc.perform(patch("/api/reviews/8/reject"))
                .andExpect(status().isNoContent());

        verify(reviewService).reject(8L, null, USER_ID);
    }

    @Test
    void should_return_pending_reviews_when_list_pending() throws Exception {
        List<ReviewListResponseDTO> response = List.of(reviewListResponse());

        when(reviewService.listPendingApproval()).thenReturn(response);

        mockMvc.perform(get("/api/reviews/admin/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(8L))
                .andExpect(jsonPath("$[0].courseId").value(1L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));

        verify(reviewService).listPendingApproval();
    }
}