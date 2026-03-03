package br.com.novalearn.platform.domain.enums;

import br.com.novalearn.platform.core.exception.business.ValidationException;

import java.util.Arrays;

public enum QuizAttemptStatus implements PersistableEnum {
    STARTED("STARTED"),
    FINISHED("FINISHED"),
    CANCELLED("CANCELLED");

    private final String code;

    QuizAttemptStatus(String code) { this.code = code; }

    @Override
    public String getCode() { return code; }

    public static QuizAttemptStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Invalid Quiz Attempt Status: " + code));
    }
}