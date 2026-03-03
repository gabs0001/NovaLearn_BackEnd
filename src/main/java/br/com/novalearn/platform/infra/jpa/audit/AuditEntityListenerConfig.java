package br.com.novalearn.platform.infra.jpa.audit;

import br.com.novalearn.platform.infra.security.provider.SecurityUserProvider;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditEntityListenerConfig {
    private final TimeProvider timeProvider;
    private final SecurityUserProvider securityUserProvider;

    public AuditEntityListenerConfig(
            TimeProvider timeProvider,
            SecurityUserProvider securityUserProvider
    ) {
        this.timeProvider = timeProvider;
        this.securityUserProvider = securityUserProvider;
    }

    @PostConstruct
    void configureListener() {
        AuditEntityListener.configure(timeProvider, securityUserProvider);
    }
}