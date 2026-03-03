package br.com.novalearn.platform.api.controllers.user;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressResponseDTO;
import br.com.novalearn.platform.domain.services.user.UserLessonProgressService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static br.com.novalearn.platform.factories.dtos.user.UserLessonProgressTestFactory.response;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserLessonProgressController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class UserLessonProgressControllerTest extends BaseControllerTest {
    @MockitoBean
    private UserLessonProgressService userLessonProgressService;

    @Test
    void should_list_all_user_lesson_progress() throws Exception {
        List<UserLessonProgressListResponseDTO> response = List.of(
                new UserLessonProgressListResponseDTO(
                        1L,
                        5L,
                        3L,
                        false,
                        25,
                        100,
                        true,
                        false
                ),
                new UserLessonProgressListResponseDTO(
                        2L,
                        5L,
                        3L,
                        false,
                        50,
                        30,
                        true,
                        false
                )
        );

        when(userLessonProgressService.listAllActive()).thenReturn(response);

        mockMvc.perform(get("/api/users/lessons").header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].progressPercent").value(25))
                .andExpect(jsonPath("$[1].progressPercent").value(50));

        verify(userLessonProgressService).listAllActive();
    }

    @Test
    void should_find_user_lesson_progress_by_id() throws Exception {
        Long id = 1L;

        UserLessonProgressResponseDTO response = response();

        when(userLessonProgressService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/users/lessons/{id}", id).header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(5L))
                .andExpect(jsonPath("$.lessonId").value(3L));

        verify(userLessonProgressService).findById(id);
    }
}