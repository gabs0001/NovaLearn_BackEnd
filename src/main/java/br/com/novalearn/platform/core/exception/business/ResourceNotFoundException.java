package br.com.novalearn.platform.core.exception.business;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resource) {
        super(resource);
    }
}