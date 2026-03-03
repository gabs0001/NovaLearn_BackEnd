package br.com.novalearn.platform.infra.gateway.security;

public interface PasswordEncoderGateway {
    String encode(String raw);
    boolean matches(String raw, String encoded);
}