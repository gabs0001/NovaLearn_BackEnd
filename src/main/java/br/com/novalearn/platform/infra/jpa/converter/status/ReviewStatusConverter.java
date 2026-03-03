package br.com.novalearn.platform.infra.jpa.converter.status;

import br.com.novalearn.platform.domain.enums.ReviewStatus;
import br.com.novalearn.platform.infra.jpa.converter.AbstractEnumConverter;

public class ReviewStatusConverter extends AbstractEnumConverter<ReviewStatus> {
    public ReviewStatusConverter() { super(ReviewStatus.class); }
}