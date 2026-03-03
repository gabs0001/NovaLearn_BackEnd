package br.com.novalearn.platform.domain.enums;

import br.com.novalearn.platform.core.exception.business.ValidationException;

import java.util.Arrays;

public enum CourseStatus implements PersistableEnum {
    DRAFT("DRAFT"),
    PUBLISHED("PUBLISHED"),
    ARCHIVED("ARCHIVED");

    private final String code;

    CourseStatus(String code) { this.code = code; }

    @Override
    public String getCode() { return code; }

    public static CourseStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Invalid Course Status: " + code));
    }
}