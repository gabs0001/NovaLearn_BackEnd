package br.com.novalearn.platform.infra.time.provider;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SystemTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime now() { return LocalDateTime.now(); }
}