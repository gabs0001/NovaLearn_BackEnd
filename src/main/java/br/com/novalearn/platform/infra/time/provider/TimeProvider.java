package br.com.novalearn.platform.infra.time.provider;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime now();
}