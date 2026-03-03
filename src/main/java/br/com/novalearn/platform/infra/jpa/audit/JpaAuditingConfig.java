package br.com.novalearn.platform.infra.jpa.audit;

import br.com.novalearn.platform.infra.security.audit.SecurityAuditorAware;
import br.com.novalearn.platform.infra.time.provider.JpaTimeProviderAdapter;
import br.com.novalearn.platform.infra.time.provider.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(
        auditorAwareRef = "auditorAware",
        dateTimeProviderRef = "dateTimeProvider"
)
public class JpaAuditingConfig {
    @Bean
    public SecurityAuditorAware auditorAware() {
        return new SecurityAuditorAware();
    }

    @Bean
    public JpaTimeProviderAdapter dateTimeProvider(TimeProvider timeProvider) {
        return new JpaTimeProviderAdapter(timeProvider);
    }
}