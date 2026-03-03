package br.com.novalearn.platform.api.events.handlers;

import br.com.novalearn.platform.domain.events.user.UserRegisteredEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserRegisteredEventHandler {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserRegisteredEvent event) {
        System.out.println("User registered: " + event.getEmail());
    }
}