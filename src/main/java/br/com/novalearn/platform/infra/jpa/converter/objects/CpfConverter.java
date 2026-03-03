package br.com.novalearn.platform.infra.jpa.converter.objects;

import br.com.novalearn.platform.domain.valueobjects.Cpf;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CpfConverter implements AttributeConverter<Cpf, String> {
    @Override
    public String convertToDatabaseColumn(Cpf attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Cpf convertToEntityAttribute(String dbData) { return dbData == null ? null : new Cpf(dbData); }
}