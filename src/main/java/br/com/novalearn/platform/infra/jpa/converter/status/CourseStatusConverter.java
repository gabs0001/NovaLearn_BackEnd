package br.com.novalearn.platform.infra.jpa.converter.status;

import br.com.novalearn.platform.domain.enums.CourseStatus;
import br.com.novalearn.platform.infra.jpa.converter.AbstractEnumConverter;

public class CourseStatusConverter extends AbstractEnumConverter<CourseStatus> {
    public CourseStatusConverter() {
        super(CourseStatus.class);
    }
}