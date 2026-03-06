package br.com.novalearn.platform.core.config;

import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.repositories.user.UserRepository;
import br.com.novalearn.platform.domain.valueobjects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner createRootUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Email rootEmail = new Email("root@novalearn.com");

            if(!userRepository.existsByEmailAndDeletedFalse(rootEmail)) {
                User root = User.builder()
                        .firstName("Root")
                        .email(rootEmail)
                        .passwordHash(passwordEncoder.encode("root123"))
                        .role(UserRole.ADMIN)
                        .emailVerified(true)
                        .build();

                userRepository.save(root);
            }
        };
    }
}