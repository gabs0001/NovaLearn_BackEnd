package br.com.novalearn.platform.api.events.handlers;

import br.com.novalearn.platform.domain.events.enrollment.EnrollmentCancelledEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EnrollmentCancelledEventHandler {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(EnrollmentCancelledEvent event) {
        System.out.println(
                "Enrollment cancelled user "
                        + event.getUserId()
                        + " course "
                        + event.getCourseId()
        );
    }
}