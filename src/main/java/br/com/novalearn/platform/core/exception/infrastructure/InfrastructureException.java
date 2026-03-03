package br.com.novalearn.platform.core.exception.infrastructure;

public class InfrastructureException extends RuntimeException {
    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}