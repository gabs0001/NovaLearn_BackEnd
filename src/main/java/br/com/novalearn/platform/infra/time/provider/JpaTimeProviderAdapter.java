package br.com.novalearn.platform.infra.time.provider;

import org.springframework.data.auditing.DateTimeProvider;

import java.time.temporal.TemporalAccessor;
import java.util.Optional;

public class JpaTimeProviderAdapter implements DateTimeProvider {
    private final TimeProvider timeProvider;

    public JpaTimeProviderAdapter(TimeProvider timeProvider) { this.timeProvider = timeProvider; }

    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(timeProvider.now());
    }
}