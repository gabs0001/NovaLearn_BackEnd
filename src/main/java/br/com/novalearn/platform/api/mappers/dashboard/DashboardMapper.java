package br.com.novalearn.platform.api.mappers.dashboard;

import br.com.novalearn.platform.api.dtos.dashboard.DashboardOverviewResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.CertificatesSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.CoursesSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.ProgressSummaryResponseDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DashboardMapper {
    public DashboardOverviewResponseDTO toOverview(
            long total,
            long enrolled,
            long inProgress,
            long completed,
            long cancelled,
            BigDecimal avgProgress,
            long certificates
    ) {
        return new DashboardOverviewResponseDTO(
                total,
                enrolled,
                inProgress,
                completed,
                cancelled,
                avgProgress,
                certificates
        );
    }

    public CoursesSummaryResponseDTO toCoursesSummary(
            long enrolled,
            long inProgress,
            long completed
    ) {
        return new CoursesSummaryResponseDTO(enrolled, inProgress, completed);
    }

    public ProgressSummaryResponseDTO toProgressSummary(BigDecimal avgProgress) {
        return new ProgressSummaryResponseDTO(avgProgress);
    }

    public CertificatesSummaryResponseDTO toCertificatesSummary(long certificates) {
        return new CertificatesSummaryResponseDTO(certificates);
    }
}