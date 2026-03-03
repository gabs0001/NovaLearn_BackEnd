package br.com.novalearn.platform.api.controllers.course;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.domain.services.course.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static br.com.novalearn.platform.factories.dtos.course.CourseTestFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class CourseControllerTest extends BaseControllerTest {
    @MockitoBean
    private CourseService courseService;

    @Test
    void should_return_active_courses_when_list() throws Exception {
        when(courseService.listAllActive()).thenReturn(List.of(courseListResponse()));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Java Fundamentals"));

        verify(courseService).listAllActive();
    }

    @Test
    void should_return_course_when_find_by_id() throws Exception {
        when(courseService.findById(1L)).thenReturn(courseResponse());

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Java Fundamentals"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        verify(courseService).findById(1L);
    }

    @Test
    void should_return_course_when_create() throws Exception {
        mock_authenticated_user();
        when(courseService.create(eq(5L), any())).thenReturn(courseResponse());

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseCreateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Java Fundamentals"));

        verify(courseService).create(eq(5L), any());
    }

    @Test
    void should_return_400_when_invalid_create() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCourseCreateRequest())))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(courseService);
    }

    @Test
    void should_use_authenticated_user_when_update() throws Exception {
        mock_authenticated_user();
        when(courseService.update(eq(1L), any(), eq(5L))).thenReturn(courseResponse());

        mockMvc.perform(patch("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseUpdateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(courseService).update(eq(1L), any(), eq(5L));
    }

    @Test
    void should_return_no_content_when_activate() throws Exception {
        mock_authenticated_user();
        mockMvc.perform(patch("/api/courses/1/activate")).andExpect(status().isNoContent());
        verify(courseService).activate(1L, 5L);
    }

    @Test
    void should_return_no_content_when_deactivate() throws Exception {
        mock_authenticated_user();
        mockMvc.perform(patch("/api/courses/1/deactivate")).andExpect(status().isNoContent());
        verify(courseService).deactivate(1L, 5L);
    }

    @Test
    void should_return_no_content_when_delete() throws Exception {
        mock_authenticated_user();
        mockMvc.perform(delete("/api/courses/1")).andExpect(status().isNoContent());
        verify(courseService).delete(1L, 5L);
    }

    @Test
    void should_return_no_content_when_restore() throws Exception {
        mock_authenticated_user();
        mockMvc.perform(patch("/api/courses/1/restore")).andExpect(status().isNoContent());
        verify(courseService).restore(1L, 5L);
    }

    @Test
    void should_return_course_when_get_by_slug() throws Exception {
        when(courseService.findBySlug("java-fundamentals")).thenReturn(courseResponse());

        mockMvc.perform(get("/api/courses/slug/java-fundamentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("java-fundamentals"));

        verify(courseService).findBySlug("java-fundamentals");
    }

    @Test
    void should_return_modules_when_get_modules_by_slug() throws Exception {
        when(courseService.listByCourseSlug("java-fundamentals")).thenReturn(List.of(moduleListResponse()));

        mockMvc.perform(get("/api/courses/slug/java-fundamentals/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("Backend basics"));

        verify(courseService).listByCourseSlug("java-fundamentals");
    }

    @Test
    void should_return_data_when_get_progress() throws Exception {
        when(courseService.getCourseProgress(1L)).thenReturn(courseProgressResponse());

        mockMvc.perform(get("/api/courses/1/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.enrollmentStatus").value("IN_PROGRESS"));

        verify(courseService).getCourseProgress(1L);
    }

    @Test
    void should_return_courses_when_get_by_category() throws Exception {
        when(courseService.listByCategory(10L)).thenReturn(List.of(courseListResponse()));

        mockMvc.perform(get("/api/courses/category/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        verify(courseService).listByCategory(10L);
    }

    @Test
    void should_return_courses_when_list_all_admin() throws Exception {
        when(courseService.listAll()).thenReturn(List.of(courseListResponse()));

        mockMvc.perform(get("/api/courses/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        verify(courseService).listAll();
    }

    @Test
    void should_return_true_when_check_name() throws Exception {
        when(courseService.existsByName("Java Fundamentals")).thenReturn(true);

        mockMvc.perform(get("/api/courses/check-name").param("name", "Java Fundamentals"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(courseService).existsByName("Java Fundamentals");
    }

    @Test
    void should_return_courses_when_select() throws Exception {
        when(courseService.listForSelect()).thenReturn(List.of(courseListResponse()));

        mockMvc.perform(get("/api/courses/select"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        verify(courseService).listForSelect();
    }

    @Test
    void should_return_courses_when_featured() throws Exception {
        when(courseService.listFeatured()).thenReturn(List.of(courseListResponse()));

        mockMvc.perform(get("/api/courses/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        verify(courseService).listFeatured();
    }
}