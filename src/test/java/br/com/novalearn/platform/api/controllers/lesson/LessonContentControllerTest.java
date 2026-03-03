package br.com.novalearn.platform.api.controllers.lesson;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentResponseDTO;
import br.com.novalearn.platform.domain.services.lesson.LessonContentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static br.com.novalearn.platform.factories.dtos.lesson.LessonContentFactoryTest.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LessonContentController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class LessonContentControllerTest extends BaseControllerTest {
    @MockitoBean
    private LessonContentService lessonContentService;

    @Test
    void should_find_lesson_content_by_lesson_id() throws Exception {
        Long lessonId = 3L;

        LessonContentResponseDTO response = lessonContentResponse();

        when(lessonContentService.findByLesson(lessonId)).thenReturn(response);

        mockMvc.perform(get("/api/lessons/{id}/content", lessonId)
                        .header("Authorization", "Bearer fake-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(8L));

        verify(lessonContentService).findByLesson(lessonId);
    }

    @Test
    void should_create_lesson_content() throws Exception {
        Long lessonId = 3L;

        mock_authenticated_user();

        LessonContentCreateRequestDTO request = lessonContentCreateRequest();
        LessonContentResponseDTO response = lessonContentResponse();

        when(lessonContentService.create(eq(lessonId), any(), eq(USER_ID))).thenReturn(response);

        mockMvc.perform(post("/api/lessons/{id}/content", lessonId)
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L));

        verify(lessonContentService).create(eq(lessonId), any(), eq(USER_ID));
    }

    @Test
    void should_return_400_when_create_request_is_invalid() throws Exception {
        Long lessonId = 3L;

        LessonContentCreateRequestDTO request = lessonContentCreateInvalidRequest();

        mockMvc.perform(post("/api/lessons/{id}/content", lessonId)
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(lessonContentService);
    }
}