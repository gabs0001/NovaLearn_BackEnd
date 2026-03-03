package br.com.novalearn.platform.infra.jpa.converter;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.enums.PersistableEnum;
import jakarta.persistence.AttributeConverter;

public abstract class AbstractEnumConverter<E extends Enum<E> & PersistableEnum> implements AttributeConverter<E, String> {
    private final Class<E> enumClass;

    protected AbstractEnumConverter(Class<E> enumClass) { this.enumClass = enumClass; }

    @Override
    public String convertToDatabaseColumn(E attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        if(dbData == null) return null;

        for(E value : enumClass.getEnumConstants()) {
            if(value.getCode().equals(dbData)) return value;
        }

        throw new ValidationException("Invalid value '" + dbData + "' for enum " + enumClass.getSimpleName());
    }
}