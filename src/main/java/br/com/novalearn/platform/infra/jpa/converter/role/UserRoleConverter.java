package br.com.novalearn.platform.infra.jpa.converter.role;

import br.com.novalearn.platform.domain.enums.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {
    @Override
    public String convertToDatabaseColumn(UserRole role) {
        return role != null ? role.name() : null;
    }

    @Override
    public UserRole convertToEntityAttribute(String value) {
        return value != null ? UserRole.valueOf(value) : null;
    }
}