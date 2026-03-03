package br.com.novalearn.platform.api.events.handlers;

import br.com.novalearn.platform.domain.events.certificate.CertificateIssuedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CertificateIssuedEventHandler {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(CertificateIssuedEvent event) {
        System.out.println(
                "Certificate issued for user "
                        + event.getUserId()
                        + " course "
                        + event.getCourseId()
        );
    }
}