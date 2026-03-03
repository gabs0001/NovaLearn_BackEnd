package br.com.novalearn.platform.infra.security.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication  = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if(principal instanceof Long userId) {
            return Optional.of(userId);
        }

        return Optional.empty();
    }
}