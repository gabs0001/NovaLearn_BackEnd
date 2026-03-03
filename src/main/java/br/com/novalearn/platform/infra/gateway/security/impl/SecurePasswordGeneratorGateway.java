package br.com.novalearn.platform.infra.gateway.security.impl;

import br.com.novalearn.platform.infra.gateway.security.PasswordGeneratorGateway;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class SecurePasswordGeneratorGateway implements PasswordGeneratorGateway {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
    private static final int DEFAULT_LENGTH = 12;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {
        StringBuilder password = new StringBuilder(DEFAULT_LENGTH);

        for(int i = 0; i < DEFAULT_LENGTH; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}