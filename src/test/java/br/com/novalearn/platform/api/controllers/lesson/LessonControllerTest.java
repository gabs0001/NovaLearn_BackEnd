package br.com.novalearn.platform.api.controllers.lesson;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.lesson.LessonCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonListResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonUpdateRequestDTO;
import br.com.novalearn.platform.domain.services.lesson.LessonService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.dtos.lesson.LessonTestFactory.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LessonController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class LessonControllerTest extends BaseControllerTest {
    @MockitoBean
    private LessonService lessonService;

    @Test
    void should_return_active_lessons_when_list() throws Exception {
        when(lessonService.listAllActive()).thenReturn(List.of(lessonListResponse()));

        mockMvc.perform(get("/api/lessons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].moduleId").value(6L))
                .andExpect(jsonPath("$[0].name").value("Java introduction"));

        verify(lessonService).listAllActive();
    }

    @Test
    void should_return_lesson_when_find_by_id() throws Exception {
        when(lessonService.findById(3L)).thenReturn(lessonResponse());

        mockMvc.perform(get("/api/lessons/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Java introduction"))
                .andExpect(jsonPath("$.sequence").value(1))
                .andExpect(jsonPath("$.durationSeconds").value(300));

        verify(lessonService).findById(3L);
    }

    @Test
    void should_return_lesson_when_create() throws Exception {
        mock_authenticated_user();

        LessonCreateRequestDTO request = lessonCreateRequest();
        LessonResponseDTO response = lessonResponse();

        when(lessonService.create(eq(5L), any())).thenReturn(response);

        mockMvc.perform(post("/api/lessons")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java introduction"));

        verify(lessonService).create(eq(5L), any());
    }

    @Test
    void should_return_400_when_invalid_create() throws Exception {
        LessonCreateRequestDTO request =
                new LessonCreateRequestDTO(
                        null,
                        "",
                        "",
                        0,
                        0,
                        null,
                        null,
                        "",
                        "",
                        ""
                );

        mockMvc.perform(post("/api/lessons")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(lessonService);
    }

    @Test
    void should_use_authenticated_user_when_update() throws Exception {
        mock_authenticated_user();

        LessonUpdateRequestDTO request = lessonUpdateRequest();

        LessonResponseDTO response = new LessonResponseDTO(
                3L,
                true,
                false,
                LocalDateTime.now(),
                null,
                "Observations",
                6L,
                "Java new introduction",
                "This is the new description",
                1,
                300,
                false,
                true,
                "http://previewurl.com",
                "New notes",
                5L,
                null
        );

        when(lessonService.update(eq(3L), any(), eq(5L))).thenReturn(response);

        mockMvc.perform(patch("/api/lessons/3")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java new introduction"))
                .andExpect(jsonPath("$.description").value("This is the new description"))
                .andExpect(jsonPath("$.notes").value("New notes"));

        verify(lessonService).update(eq(3L), any(), eq(5L));
    }

    @Test
    void should_return_no_content_when_activate() throws Exception {
        mock_authenticated_user();

        doNothing().when(lessonService).activate(3L, 5L);

        mockMvc.perform(patch("/api/lessons/3/activate")).andExpect(status().isNoContent());

        verify(lessonService).activate(3L, 5L);
    }

    @Test
    void should_return_no_content_when_deactivate() throws Exception {
        mock_authenticated_user();

        doNothing().when(lessonService).deactivate(3L, 5L);

        mockMvc.perform(patch("/api/lessons/3/deactivate")).andExpect(status().isNoContent());

        verify(lessonService).deactivate(3L, 5L);
    }

    @Test
    void should_return_no_content_when_delete() throws Exception {
        mock_authenticated_user();

        doNothing().when(lessonService).delete(3L, 5L);

        mockMvc.perform(delete("/api/lessons/3")).andExpect(status().isNoContent());

        verify(lessonService).delete(3L, 5L);
    }

    @Test
    void should_return_no_content_when_restore() throws Exception {
        mock_authenticated_user();

        doNothing().when(lessonService).restore(3L, 5L);

        mockMvc.perform(patch("/api/lessons/3/restore")).andExpect(status().isNoContent());

        verify(lessonService).restore(3L, 5L);
    }

    @Test
    void should_return_lessons_when_list_by_module() throws Exception {
        LessonListResponseDTO dto = lessonListResponse();

        when(lessonService.listByModule(6L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/lessons/module/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Java introduction"));

        verify(lessonService).listByModule(6L);
    }

    @Test
    void should_return_lessons_when_list_all_admin() throws Exception {
        LessonListResponseDTO dto = lessonListResponse();

        when(lessonService.listAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/lessons/admin")).andExpect(status().isOk());

        verify(lessonService).listAll();
    }

    @Test
    void should_return_boolean_when_check_sequence() throws Exception {
        when(lessonService.existsSequence(6L, 1)).thenReturn(true);

        mockMvc.perform(get("/api/lessons/check-sequence")
                        .param("moduleId", "6")
                        .param("sequence", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(lessonService).existsSequence(6L, 1);
    }

    @Test
    void should_return_no_content_when_reorder() throws Exception {
        mock_authenticated_user();

        List<Long> ids = reorderIds();

        doNothing().when(lessonService).reorder(eq(6L), any(), eq(5L));

        mockMvc.perform(patch("/api/lessons/module/6/reorder")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isNoContent());

        verify(lessonService).reorder(eq(6L), any(), eq(5L));
    }
}