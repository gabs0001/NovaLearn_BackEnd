package br.com.novalearn.platform.api.dtos.dashboard.summary;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgressSummaryResponseDTO {
    private final BigDecimal averageProgress;

    public ProgressSummaryResponseDTO(BigDecimal averageProgress) {
        this.averageProgress = averageProgress;
    }

    public BigDecimal getAverageProgress() { return averageProgress; }
}