package br.com.novalearn.platform.domain.entities.base;

import java.time.LocalDateTime;

public interface Auditable {
    void setCreatedAt(LocalDateTime createdAt);
    void setUpdatedAt(LocalDateTime updatedAt);
    void setCreatedBy(Long createdBy);
    void setUpdatedBy(Long updatedBy);
}