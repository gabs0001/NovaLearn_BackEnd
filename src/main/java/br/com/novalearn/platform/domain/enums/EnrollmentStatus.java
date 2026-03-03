package br.com.novalearn.platform.domain.enums;

import br.com.novalearn.platform.core.exception.business.ValidationException;

import java.util.Arrays;

public enum EnrollmentStatus implements PersistableEnum {
    ENROLLED("ENROLLED"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    REFUNDED("REFUNDED"),
    EXPIRED("EXPIRED");

    private final String code;

    EnrollmentStatus(String code) { this.code = code; }

    @Override
    public String getCode() { return code; }

    public static EnrollmentStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Invalid Enrollment Status: " + code));
    }
}