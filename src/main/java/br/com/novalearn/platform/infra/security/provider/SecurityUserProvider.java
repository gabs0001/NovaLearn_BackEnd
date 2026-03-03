package br.com.novalearn.platform.infra.security.provider;

import java.util.Optional;

public interface SecurityUserProvider {
    Optional<Long> getCurrentUserId();
}