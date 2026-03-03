package br.com.novalearn.platform.api.controllers.dashboard;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.dashboard.DashboardOverviewResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.*;
import br.com.novalearn.platform.domain.services.dashboard.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static br.com.novalearn.platform.factories.dtos.dashboard.DashboardTestFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class DashboardControllerTest extends BaseControllerTest {
    @MockitoBean
    private DashboardService dashboardService;

    @Test
    void should_return_dashboard_overview_when_requesting_dashboard() throws Exception {
        DashboardOverviewResponseDTO response = dashboardOverviewResponse();

        when(dashboardService.getDashboard()).thenReturn(response);

        mockMvc.perform(get("/api/me/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEnrollments").value(10L))
                .andExpect(jsonPath("$.completedCourses").value(2L))
                .andExpect(jsonPath("$.averageProgress").value(50.0));

        verify(dashboardService).getDashboard();
    }

    @Test
    void should_return_courses_summary_when_requesting_courses_summary() throws Exception {
        CoursesSummaryResponseDTO response = coursesSummaryResponse();

        when(dashboardService.getCoursesSummary()).thenReturn(response);

        mockMvc.perform(get("/api/me/dashboard/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrolled").value(4L))
                .andExpect(jsonPath("$.inProgress").value(2L))
                .andExpect(jsonPath("$.completed").value(2L));

        verify(dashboardService).getCoursesSummary();
    }


    @Test
    void should_return_progress_summary_when_requesting_progress_summary() throws Exception {
        ProgressSummaryResponseDTO response = progressSummaryResponse();

        when(dashboardService.getProgressSummary()).thenReturn(response);

        mockMvc.perform(get("/api/me/dashboard/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageProgress").value(50.0));

        verify(dashboardService).getProgressSummary();
    }

    @Test
    void should_return_certificates_summary_when_requesting_certificates_summary() throws Exception {
        CertificatesSummaryResponseDTO response = certificatesSummaryResponse();

        when(dashboardService.getCertificatesSummary()).thenReturn(response);

        mockMvc.perform(get("/api/me/dashboard/certificates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificatesIssued").value(10L));

        verify(dashboardService).getCertificatesSummary();
    }
}