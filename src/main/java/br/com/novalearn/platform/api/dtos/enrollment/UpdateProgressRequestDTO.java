package br.com.novalearn.platform.api.dtos.enrollment;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProgressRequestDTO {
    @NotNull
    @Min(0)
    @Max(100)
    private Integer progressPercent;

    public UpdateProgressRequestDTO() {}

    public UpdateProgressRequestDTO(Integer progressPercent) {
        this.progressPercent = progressPercent;
    }

    public Integer getProgressPercent() { return progressPercent; }

    public void setProgressPercent(Integer progressPercent) { this.progressPercent = progressPercent; }
}