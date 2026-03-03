package br.com.novalearn.platform.factories.dtos.dashboard;

import br.com.novalearn.platform.api.dtos.dashboard.DashboardOverviewResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.CertificatesSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.CoursesSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.ProgressSummaryResponseDTO;

import java.math.BigDecimal;

public final class DashboardTestFactory {
    public static DashboardOverviewResponseDTO dashboardOverviewResponse() {
        return new DashboardOverviewResponseDTO(
                10L,
                4L,
                2L,
                2L,
                0L,
                new BigDecimal("50.0"),
                10L
        );
    }

    public static CoursesSummaryResponseDTO coursesSummaryResponse() {
        return new CoursesSummaryResponseDTO(4L, 2L, 2L);
    }

    public static ProgressSummaryResponseDTO progressSummaryResponse() {
        return new ProgressSummaryResponseDTO(new BigDecimal("50.0"));
    }

    public static CertificatesSummaryResponseDTO certificatesSummaryResponse() {
        return new CertificatesSummaryResponseDTO(10L);
    }
}