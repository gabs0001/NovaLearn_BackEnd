package br.com.novalearn.platform.api.dtos.quiz.summary;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizSummaryResponseDTO {
    private Long quizId;
    private String name;
    private int totalQuestions;
    private int answeredQuestions;
    private int correctAnswers;
    private BigDecimal bestScore;
    private BigDecimal lastScore;
    private boolean passed;
    private Integer attemptsUsed;
    private Integer maxAttempts;

    public QuizSummaryResponseDTO() {}

    public QuizSummaryResponseDTO(
            Long quizId,
            String name,
            int totalQuestions,
            int answeredQuestions,
            int correctAnswers,
            BigDecimal bestScore,
            BigDecimal lastScore,
            boolean passed,
            Integer attemptsUsed,
            Integer maxAttempts
    ) {
        this.quizId = quizId;
        this.name = name;
        this.totalQuestions = totalQuestions;
        this.answeredQuestions = answeredQuestions;
        this.correctAnswers = correctAnswers;
        this.bestScore = bestScore;
        this.lastScore = lastScore;
        this.passed = passed;
        this.attemptsUsed = attemptsUsed;
        this.maxAttempts = maxAttempts;
    }

    public Long getQuizId() { return quizId; }

    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getTotalQuestions() { return totalQuestions; }

    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getAnsweredQuestions() { return answeredQuestions; }

    public void setAnsweredQuestions(int answeredQuestions) { this.answeredQuestions = answeredQuestions; }

    public int getCorrectAnswers() { return correctAnswers; }

    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public BigDecimal getBestScore() { return bestScore; }

    public void setBestScore(BigDecimal bestScore) { this.bestScore = bestScore; }

    public BigDecimal getLastScore() { return lastScore; }

    public void setLastScore(BigDecimal lastScore) { this.lastScore = lastScore; }

    public boolean isPassed() { return passed; }

    public void setPassed(boolean passed) { this.passed = passed; }

    public Integer getAttemptsUsed() { return attemptsUsed; }

    public void setAttemptsUsed(Integer attemptsUsed) { this.attemptsUsed = attemptsUsed; }

    public Integer getMaxAttempts() { return maxAttempts; }

    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }
}