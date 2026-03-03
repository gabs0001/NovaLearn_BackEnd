package br.com.novalearn.platform.core.exception.business;

public class ForbiddenOperationException extends BusinessException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
