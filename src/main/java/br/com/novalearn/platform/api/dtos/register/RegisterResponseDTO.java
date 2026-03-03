package br.com.novalearn.platform.api.dtos.register;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponseDTO {
    private Long userId;
    private String email;
    private Boolean emailVerified;
    private LocalDateTime createdAt;

    protected RegisterResponseDTO() {}

    public RegisterResponseDTO(Long userId, String email, Boolean emailVerified, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
    }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public Boolean getEmailVerified() { return emailVerified; }

    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}