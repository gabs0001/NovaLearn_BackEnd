package br.com.novalearn.platform.domain.valueobjects;

import br.com.novalearn.platform.core.exception.business.ValidationException;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    public Email(String value) {
        if(value == null || value.isBlank()) {
            throw new ValidationException("Email cannot be null or blank.");
        }

        String normalized = value.trim().toLowerCase();

        if(!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new ValidationException("Invalid email format.");
        }

        this.value = normalized;
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Email email)) return false;
        return value.equals(email.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value; }
}