package br.com.novalearn.platform.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseDTO {
    private Long id;
    private Boolean active;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String observations;

    public BaseDTO() {}

    public BaseDTO(
            Long id,
            Boolean active,
            Boolean deleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String observations
    ) {
        this.id = id;
        this.active = active;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.observations = observations;
    }

    public Long getId() { return id; }
    public Boolean getActive() { return active; }
    public Boolean getDeleted() { return deleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getObservations() { return observations; }

    public void setId(Long id) { this.id = id; }
    public void setActive(Boolean active) { this.active = active; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setObservations(String observations) { this.observations = observations; }
}