package br.com.novalearn.platform.infra.gateway.notification;

public interface NotificationGateway {
    void notify(
            String destination,
            String subject,
            String message
    );
}