package br.com.novalearn.platform.infra.gateway.notification.impl;

import br.com.novalearn.platform.infra.gateway.notification.NotificationGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogNotificationGateway implements NotificationGateway {
    private static final Logger log = LoggerFactory.getLogger(LogNotificationGateway.class);

    @Override
    public void notify(String destination, String subject, String message) {
        log.info(
                """
                 \n 🔔 Notification sent
                 → Destination: {}
                 → Subject: {}
                 → Message: {}
                 """,
                destination,
                subject,
                message
        );
    }
}