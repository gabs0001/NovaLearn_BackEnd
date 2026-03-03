package br.com.novalearn.platform.core.exception.business;

public abstract class BusinessException extends RuntimeException {
    protected BusinessException(String message) { super(message); }
}