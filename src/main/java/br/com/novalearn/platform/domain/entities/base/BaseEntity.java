package br.com.novalearn.platform.domain.entities.base;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.converters.BooleanToYNConverter;
import br.com.novalearn.platform.domain.events.DomainEvent;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MappedSuperclass
public abstract class BaseEntity implements Auditable {
    @Column(name = "cod_created_by")
    private Long createdBy;

    @Column(name = "cod_updated_by")
    private Long updatedBy;

    @Column(name = "dat_created", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "dat_updated")
    private LocalDateTime updatedAt;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_active", columnDefinition = "char(1)", nullable = false)
    protected boolean active = true;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(name = "ind_deleted", columnDefinition = "char(1)", nullable = false)
    protected boolean deleted = false;

    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public void activate() {
        ensureNotDeleted();
        this.active = true;
    }

    public void deactivate() {
        ensureNotDeleted();
        this.active = false;
    }

    public void markAsDeleted() {
        this.active = false;
        this.deleted = true;
    }

    public void markAsNotDeleted() {
        this.deleted = false;
    }

    public void delete() {
        if(this.deleted) throw new ForbiddenOperationException("Entity is already deleted.");
        this.active = false;
        this.deleted = true;
    }

    public void restore() {
        if(!this.deleted) throw new ForbiddenOperationException("Entity is not deleted.");
        this.active = true;
        this.deleted = false;
    }

    public static String sanitize(String value) {
        if(value == null) return null;

        String sanitized = value.trim();
        return sanitized.isEmpty() ? null : sanitized;
    }

    public void ensureNotDeleted() {
        if(this.deleted) throw new ForbiddenOperationException("Deleted entity cannot be modified.");
    }

    public void auditCreate(Long actorId, LocalDateTime now) {
        if(actorId == null) {
            throw new ValidationException("Actor is required for audit create");
        }

        if(now == null) {
            throw new ValidationException("Date is required for audit create");
        }

        this.active = true;
        this.deleted = false;

        this.createdBy = actorId;
        this.createdAt = now;

        this.updatedBy = actorId;
        this.updatedAt = now;
    }

    public void auditUpdate(Long actorId, LocalDateTime now) {
        if(actorId == null) throw new ValidationException("actorId cannot be null");
        if(now == null) throw new ValidationException("timestamp cannot be null");

        this.updatedBy = actorId;
        this.updatedAt = now;
    }

    protected void registerEvent(DomainEvent event) {
        if(event == null) throw new ValidationException("Domain event cannot be null");
        this.domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public Long getCreatedBy() { return createdBy; }
    public Long getUpdatedBy() { return updatedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isActive() { return active; }
    public boolean isDeleted() { return deleted; }

    protected void setActive(boolean active) { this.active = active; }
    protected void setDeleted(boolean deleted) { this.deleted = deleted; }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    @Override
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", active=" + active +
                ", deleted=" + deleted +
                '}';
    }
}