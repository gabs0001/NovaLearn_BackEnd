package br.com.novalearn.platform.domain.enums;

import br.com.novalearn.platform.core.exception.business.ValidationException;

import java.util.Arrays;

public enum UserStatus implements PersistableEnum {
    PENDING("PENDING"),
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    SUSPENDED("SUSPENDED"),
    BLOCKED("BLOCKED"),
    DELETED("DELETED");

    private final String code;

    UserStatus(String code) { this.code = code; }

    @Override
    public String getCode() { return code; }

    public static UserStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Invalid User Status: " + code));
    }
}