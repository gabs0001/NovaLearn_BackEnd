package br.com.novalearn.platform.core.exception.business;

public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super(message);
    }
}