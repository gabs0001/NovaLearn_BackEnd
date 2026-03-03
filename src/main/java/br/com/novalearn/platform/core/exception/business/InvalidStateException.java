package br.com.novalearn.platform.core.exception.business;

public class InvalidStateException extends BusinessException {
       public InvalidStateException(String message) {
           super(message);
       }
}