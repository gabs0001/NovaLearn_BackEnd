package br.com.novalearn.platform.api.dtos.user.activity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserActivityResponseDTO {
    private Long userId;
    private long totalEnrollments;
    private long completedCourses;
    private long quizzesAttempted;
    private long quizzesPassed;
    private LocalDateTime lastLoginAt;
    private LocalDateTime lastActivityAt;

    public UserActivityResponseDTO() {}

    public UserActivityResponseDTO(
            Long userId,
            long totalEnrollments,
            long completedCourses,
            long quizzesAttempted,
            long quizzesPassed,
            LocalDateTime lastLoginAt,
            LocalDateTime lastActivityAt
    ) {
        this.userId = userId;
        this.totalEnrollments = totalEnrollments;
        this.completedCourses = completedCourses;
        this.quizzesAttempted = quizzesAttempted;
        this.quizzesPassed = quizzesPassed;
        this.lastLoginAt = lastLoginAt;
        this.lastActivityAt = lastActivityAt;
    }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public long getTotalEnrollments() { return totalEnrollments; }

    public void setTotalEnrollments(long totalEnrollments) { this.totalEnrollments = totalEnrollments; }

    public long getCompletedCourses() { return completedCourses; }

    public void setCompletedCourses(long completedCourses) { this.completedCourses = completedCourses; }

    public long getQuizzesAttempted() { return quizzesAttempted; }

    public void setQuizzesAttempted(long quizzesAttempted) { this.quizzesAttempted = quizzesAttempted; }

    public long getQuizzesPassed() { return quizzesPassed; }

    public void setQuizzesPassed(long quizzesPassed) { this.quizzesPassed = quizzesPassed; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }

    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public LocalDateTime getLastActivityAt() { return lastActivityAt; }

    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
}