package br.com.novalearn.platform.infra.jpa.converter.status;

import br.com.novalearn.platform.domain.enums.QuizAttemptStatus;
import br.com.novalearn.platform.infra.jpa.converter.AbstractEnumConverter;

public class QuizAttemptStatusConverter extends AbstractEnumConverter<QuizAttemptStatus> {
    public QuizAttemptStatusConverter() {
        super(QuizAttemptStatus.class);
    }
}