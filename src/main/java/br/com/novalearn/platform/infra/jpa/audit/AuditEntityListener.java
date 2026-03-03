package br.com.novalearn.platform.infra.jpa.audit;

import br.com.novalearn.platform.domain.entities.base.Auditable;
import br.com.novalearn.platform.infra.security.provider.SecurityUserProvider;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

public class AuditEntityListener {
    private static TimeProvider timeProvider;
    private static SecurityUserProvider securityUserProvider;

    public static void configure(
            TimeProvider timeProvider,
            SecurityUserProvider securityUserProvider
    ) {
        AuditEntityListener.timeProvider = timeProvider;
        AuditEntityListener.securityUserProvider = securityUserProvider;
    }

    @PrePersist
    public void prePersist(Object entity) {
        if(!(entity instanceof Auditable auditable)) return;
        LocalDateTime now = timeProvider.now();
        Long userId = resolveCurrentUserId();

        auditable.setCreatedAt(now);
        auditable.setUpdatedAt(now);
        auditable.setCreatedBy(userId);
        auditable.setUpdatedBy(userId);
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof Auditable auditable) {
            auditable.setUpdatedAt(timeProvider.now());
            auditable.setUpdatedBy(resolveCurrentUserId());
        }
    }

    private Long resolveCurrentUserId() { return securityUserProvider.getCurrentUserId().orElse(null); }
}