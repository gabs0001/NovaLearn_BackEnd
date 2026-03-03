package br.com.novalearn.platform.infra.persistence;

import br.com.novalearn.platform.core.exception.infrastructure.InfrastructureException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class JpaExceptionTranslator {
    public RuntimeException translate(Exception ex) {
        if(ex instanceof EntityNotFoundException) {
            return (RuntimeException) ex;
        }

        if(ex instanceof DataIntegrityViolationException) {
            return handleDataIntegrityViolation((DataIntegrityViolationException) ex);
        }

        if(ex instanceof PersistenceException) {
            return new InfrastructureException("Persistence error ocurred", ex);
        }

        return new InfrastructureException("Unexpected database error", ex);
    }

    private RuntimeException handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Throwable cause = ex.getCause();

        if(cause instanceof ConstraintViolationException constraintEx) {
            String constraintName = constraintEx.getConstraintName();
            return new InfrastructureException("Database constraint violation: " + constraintName, ex);
        }

        return new InfrastructureException("Data integrity violation", ex);
    }
}