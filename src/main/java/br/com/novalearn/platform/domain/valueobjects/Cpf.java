package br.com.novalearn.platform.domain.valueobjects;

import br.com.novalearn.platform.core.exception.business.ValidationException;

import java.util.Objects;

public final class Cpf {
    private final String value;

    public Cpf(String value) {
        if(value == null || value.isBlank()) {
            throw new ValidationException("Cpf cannot be null or blank.");
        }

        String normalized = value.replaceAll("\\D", "");

        if(!normalized.matches("\\d{11}")) {
            throw new ValidationException("Invalid CPF format.");
        }

        this.value = normalized;
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Cpf cpf)) return false;
        return value.equals(cpf.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }
}