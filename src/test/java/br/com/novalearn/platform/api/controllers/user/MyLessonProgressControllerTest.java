package br.com.novalearn.platform.api.controllers.user;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressResponseDTO;
import br.com.novalearn.platform.domain.services.user.MyLessonProgressService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.dtos.user.UserLessonProgressTestFactory.listResponse;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MyLessonProgressController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class MyLessonProgressControllerTest extends BaseControllerTest {
    @MockitoBean
    private MyLessonProgressService myLessonProgressService;

    @Test
    void should_list_my_lesson_progress() throws Exception {
        List<UserLessonProgressListResponseDTO> response = List.of(listResponse());

        mock_authenticated_user();
        when(myLessonProgressService.listMyProgress(USER_ID)).thenReturn(response);

        mockMvc.perform(get("/api/me/lessons/progress").header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].lessonId").value(3L));

        verify(myLessonProgressService).listMyProgress(USER_ID);
    }

    @Test
    void should_complete_lesson() throws Exception {
        Long lessonId = 3L;

        UserLessonProgressResponseDTO response = new UserLessonProgressResponseDTO(
                1L,
                true,
                false,
                LocalDateTime.now(),
                null,
                "Observations",
                USER_ID,
                lessonId,
                true,
                null,
                100,
                50,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusHours(4),
                USER_ID,
                null
        );

        mock_authenticated_user();
        when(myLessonProgressService.completeLesson(lessonId, USER_ID)).thenReturn(response);

        mockMvc.perform(patch("/api/me/lessons/{lessonId}/complete", lessonId)
                .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lessonId").value(3L))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.progressPercent").value(100));

        verify(myLessonProgressService).completeLesson(lessonId, USER_ID);
    }
}