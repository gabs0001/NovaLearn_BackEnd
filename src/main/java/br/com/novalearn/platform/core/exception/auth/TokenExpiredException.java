package br.com.novalearn.platform.core.exception.auth;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}