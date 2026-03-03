package br.com.novalearn.platform.api.controllers.enrollment;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentRequestDTO;
import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentResponseDTO;
import br.com.novalearn.platform.api.dtos.enrollment.UpdateProgressRequestDTO;
import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import br.com.novalearn.platform.domain.services.enrollment.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.dtos.enrollment.EnrollmentTestFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class EnrollmentControllerTest extends BaseControllerTest {
    @MockitoBean
    private EnrollmentService enrollmentService;

    @Test
    void should_return_my_enrollments_when_list() throws Exception {
        when(enrollmentService.listMyEnrollments()).thenReturn(List.of(enrollmentResponse()));

        mockMvc.perform(get("/api/me/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].enrollmentId").value(8L))
                .andExpect(jsonPath("$[0].courseName").value("Java Fundamentals"))
                .andExpect(jsonPath("$[0].enrollmentStatus").value("IN_PROGRESS"));

        verify(enrollmentService).listMyEnrollments();
    }

    @Test
    void should_return_enrollment_when_get_details() throws Exception {
        when(enrollmentService.getEnrollmentDetails(1L)).thenReturn(enrollmentResponse());

        mockMvc.perform(get("/api/me/enrollments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value(8L))
                .andExpect(jsonPath("$.enrollmentStatus").value("IN_PROGRESS"));

        verify(enrollmentService).getEnrollmentDetails(1L);
    }

    @Test
    void should_return_created_when_enroll() throws Exception {
        when(enrollmentService.enroll(any())).thenReturn(enrollmentResponse());

        mockMvc.perform(post("/api/me/enrollments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(enrollmentRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enrollmentId").exists())
                .andExpect(jsonPath("$.courseId").value(1L));

        verify(enrollmentService).enroll(any());
    }

    @Test
    void should_return_400_when_invalid_enroll() throws Exception {
        EnrollmentRequestDTO request = new EnrollmentRequestDTO(
                null,
                null,
                "",
                ""
        );

        mockMvc.perform(post("/api/me/enrollments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(enrollmentService);
    }

    @Test
    void should_return_updated_enrollment_when_update_progress() throws Exception {
        UpdateProgressRequestDTO request = updateProgressRequest();
        EnrollmentResponseDTO response = new EnrollmentResponseDTO(
                8L,
                1L,
                "Java Fundamentals",
                EnrollmentStatus.IN_PROGRESS,
                LocalDateTime.now().minusDays(10),
                25
        );

        when(enrollmentService.updateProgress(eq(1L), any())).thenReturn(response);

        mockMvc.perform(patch("/api/me/enrollments/1/progress")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercent").value(25));

        verify(enrollmentService).updateProgress(eq(1L), any());
    }

    @Test
    void should_return_enrollment_when_issue_certificate() throws Exception {
        EnrollmentResponseDTO response = enrollmentResponse();

        when(enrollmentService.issueCertificate(1L)).thenReturn(response);

        mockMvc.perform(post("/api/me/enrollments/1/certificate"))
                .andExpect(status().isOk());

        verify(enrollmentService).issueCertificate(1L);
    }

    @Test
    void should_return_enrollment_when_cancel_enrollment() throws Exception {
        EnrollmentResponseDTO response = enrollmentResponse();

        when(enrollmentService.cancelEnrollment(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/me/enrollments/1"))
                .andExpect(status().isOk());

        verify(enrollmentService).cancelEnrollment(1L);
    }
}