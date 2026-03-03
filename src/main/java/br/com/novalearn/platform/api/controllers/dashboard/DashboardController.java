package br.com.novalearn.platform.api.controllers.dashboard;

import br.com.novalearn.platform.api.dtos.dashboard.DashboardOverviewResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.CertificatesSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.CoursesSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.ProgressSummaryResponseDTO;
import br.com.novalearn.platform.domain.services.dashboard.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardOverviewResponseDTO> getDashboard() {
        DashboardOverviewResponseDTO response = dashboardService.getDashboard();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses")
    public ResponseEntity<CoursesSummaryResponseDTO> getCoursesSummary() {
        CoursesSummaryResponseDTO response = dashboardService.getCoursesSummary();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/progress")
    public ResponseEntity<ProgressSummaryResponseDTO> getProgressSummary() {
        ProgressSummaryResponseDTO response = dashboardService.getProgressSummary();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/certificates")
    public ResponseEntity<CertificatesSummaryResponseDTO> getCertificatesSummary() {
        CertificatesSummaryResponseDTO response = dashboardService.getCertificatesSummary();
        return ResponseEntity.ok(response);
    }
}