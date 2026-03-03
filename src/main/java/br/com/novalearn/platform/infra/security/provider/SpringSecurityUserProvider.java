package br.com.novalearn.platform.infra.security.provider;

import br.com.novalearn.platform.infra.security.userdetails.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityUserProvider implements SecurityUserProvider {
    @Override
    public Optional<Long> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if(principal instanceof CustomUserDetails userDetails) {
            return Optional.ofNullable(userDetails.getId());
        }

        return Optional.empty();
    }
}