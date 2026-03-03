package br.com.novalearn.platform.core.exception.business;

public class AlreadyDeletedException extends BusinessException {
    public AlreadyDeletedException(String message) {
        super(message);
    }
}