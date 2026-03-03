package br.com.novalearn.platform.domain.enums;

import br.com.novalearn.platform.core.exception.business.ValidationException;

import java.util.Arrays;

public enum ReviewStatus implements PersistableEnum {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String code;

    ReviewStatus(String code) { this.code = code; }

    @Override
    public String getCode() { return code; }

    public static ReviewStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Invalid Review Status: " + code));
    }
}
