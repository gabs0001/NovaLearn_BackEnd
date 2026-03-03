package br.com.novalearn.platform.api.controllers.course;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.course.my.MyCourseResponseDTO;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import br.com.novalearn.platform.domain.services.course.MyCourseService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.dtos.course.MyCourseTestFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MyCourseController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class MyCourseControllerTest extends BaseControllerTest {
    @MockitoBean
    private MyCourseService myCourseService;

    @Test
    void should_return_list_when_get_my_courses() throws Exception {
        when(myCourseService.listMyCourses()).thenReturn(List.of(myCourseResponse()));

        mockMvc.perform(get("/api/me/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Java Fundamentals"));

        verify(myCourseService).listMyCourses();
    }

    @Test
    void should_return_course_when_get_my_course_by_id() throws Exception {
        when(myCourseService.getMyCourseById(1L))
                .thenReturn(myCourseResponse());

        mockMvc.perform(get("/api/me/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(1L))
                .andExpect(jsonPath("$.enrollmentStatus").value("IN_PROGRESS"));

        verify(myCourseService).getMyCourseById(1L);
    }

    @Test
    void should_return_list_when_get_completed_courses() throws Exception {
        MyCourseResponseDTO dto = myCourseCompletedResponse();

        when(myCourseService.listCompletedCourses()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/me/courses/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Java Fundamentals"))
                .andExpect(jsonPath("$[0].enrollmentStatus").value("COMPLETED"));

        verify(myCourseService).listCompletedCourses();
    }

    @Test
    void should_return_list_when_get_courses_in_progress() throws Exception {
        MyCourseResponseDTO dto = myCourseResponse();

        when(myCourseService.listCoursesInProgress()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/me/courses/in-progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Java Fundamentals"))
                .andExpect(jsonPath("$[0].enrollmentStatus").value("IN_PROGRESS"));

        verify(myCourseService).listCoursesInProgress();
    }
}