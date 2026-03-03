package br.com.novalearn.platform.infra.jpa.converter.status;

import br.com.novalearn.platform.domain.enums.EnrollmentStatus;
import br.com.novalearn.platform.infra.jpa.converter.AbstractEnumConverter;

public class EnrollmentStatusConverter extends AbstractEnumConverter<EnrollmentStatus> {
    public EnrollmentStatusConverter() {
        super(EnrollmentStatus.class);
    }
}