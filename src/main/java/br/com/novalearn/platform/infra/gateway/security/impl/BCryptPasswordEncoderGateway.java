package br.com.novalearn.platform.infra.gateway.security.impl;

import br.com.novalearn.platform.infra.gateway.security.PasswordEncoderGateway;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderGateway implements PasswordEncoderGateway {
    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordEncoderGateway(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) { return passwordEncoder.encode(rawPassword); }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) { return passwordEncoder.matches(rawPassword, encodedPassword); }
}