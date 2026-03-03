package br.com.novalearn.platform.domain.services;

import br.com.novalearn.platform.core.exception.business.ResourceNotFoundException;
import br.com.novalearn.platform.domain.entities.base.BaseEntity;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseCrudService<E extends BaseEntity> {
    protected final JpaRepository<E, Long> repository;
    protected final String entityName;
    protected final TimeProvider timeProvider;

    protected BaseCrudService(
            JpaRepository<E, Long> repository,
            String entityName,
            TimeProvider timeProvider
    ) {
        this.repository = repository;
        this.entityName = entityName;
        this.timeProvider = timeProvider;
    }

    protected E findEntityOrThrow(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(entityName + " not found. Id=" + id)
        );
    }

    protected void applyAuditCreate(E entity, Long userId) {
        var now = timeProvider.now();

        entity.setCreatedBy(userId);
        entity.setCreatedAt(now);
        entity.setUpdatedBy(userId);
        entity.setUpdatedAt(now);
    }

    protected void applyAuditUpdate(E entity, Long userId) {
        entity.setUpdatedBy(userId);
        entity.setUpdatedAt(timeProvider.now());
    }

    @Transactional
    public void activate(Long id, Long userId) {
        E entity = findEntityOrThrow(id);

        entity.activate();
        applyAuditUpdate(entity, userId);

        repository.save(entity);
    }

    @Transactional
    public void deactivate(Long id, Long userId) {
        E entity = findEntityOrThrow(id);

        entity.deactivate();
        applyAuditUpdate(entity, userId);

        repository.save(entity);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        E entity = findEntityOrThrow(id);

        entity.delete();
        applyAuditUpdate(entity, userId);

        repository.save(entity);
    }

    @Transactional
    public void restore(Long id, Long userId) {
        E entity = findEntityOrThrow(id);

        entity.restore();
        applyAuditUpdate(entity, userId);

        repository.save(entity);
    }
}