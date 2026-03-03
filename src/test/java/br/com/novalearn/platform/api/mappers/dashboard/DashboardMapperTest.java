package br.com.novalearn.platform.api.mappers.dashboard;

import br.com.novalearn.platform.api.dtos.dashboard.DashboardOverviewResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.CertificatesSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.CoursesSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.dashboard.summary.ProgressSummaryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DashboardMapperTest {
    private DashboardMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new DashboardMapper();
    }

    @Test
    void should_map_to_dashboard_overview() {
        long total = 100;
        long enrolled = 80;
        long inProgress = 50;
        long completed = 25;
        long cancelled = 5;

        BigDecimal avgProgress = new BigDecimal("62.50");

        long certificates = 20;

        DashboardOverviewResponseDTO dto = mapper.toOverview(
                        total,
                        enrolled,
                        inProgress,
                        completed,
                        cancelled,
                        avgProgress,
                        certificates
        );

        assertThat(dto).isNotNull();

        assertThat(dto.getTotalEnrollments()).isEqualTo(total);
        assertThat(dto.getEnrolledCourses()).isEqualTo(enrolled);
        assertThat(dto.getInProgressCourses()).isEqualTo(inProgress);
        assertThat(dto.getCompletedCourses()).isEqualTo(completed);
        assertThat(dto.getCancelledCourses()).isEqualTo(cancelled);

        assertThat(dto.getAverageProgress()).isEqualByComparingTo(avgProgress);
        assertThat(dto.getCertificatesIssued()).isEqualTo(certificates);
    }

    @Test
    void should_map_to_courses_summary() {
        long enrolled = 40;
        long inProgress = 25;
        long completed = 15;

        CoursesSummaryResponseDTO dto = mapper.toCoursesSummary(enrolled, inProgress, completed);

        assertThat(dto).isNotNull();

        assertThat(dto.getEnrolled()).isEqualTo(enrolled);
        assertThat(dto.getInProgress()).isEqualTo(inProgress);
        assertThat(dto.getCompleted()).isEqualTo(completed);
    }

    @Test
    void should_map_to_progress_summary() {
        BigDecimal avgProgress = new BigDecimal("78.35");

        ProgressSummaryResponseDTO dto = mapper.toProgressSummary(avgProgress);

        assertThat(dto).isNotNull();

        assertThat(dto.getAverageProgress()).isEqualByComparingTo(avgProgress);
    }

    @Test
    void should_map_to_certificates_summary() {
        long certificates = 12;

        CertificatesSummaryResponseDTO dto = mapper.toCertificatesSummary(certificates);

        assertThat(dto).isNotNull();
        assertThat(dto.getCertificatesIssued()).isEqualTo(certificates);
    }
}