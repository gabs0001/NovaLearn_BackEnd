package br.com.novalearn.platform.api.dtos.dashboard.summary;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoursesSummaryResponseDTO {
    private final long enrolled;
    private final long inProgress;
    private final long completed;

    public CoursesSummaryResponseDTO(
            long enrolled,
            long inProgress,
            long completed
    ) {
        this.enrolled = enrolled;
        this.inProgress = inProgress;
        this.completed = completed;
    }

    public long getEnrolled() { return enrolled; }
    public long getInProgress() { return inProgress; }
    public long getCompleted() { return completed; }
}