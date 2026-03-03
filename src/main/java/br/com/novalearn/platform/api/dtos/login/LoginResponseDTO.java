package br.com.novalearn.platform.api.dtos.login;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String accessToken, String refreshToken, Long expiresIn, String tokenType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
    }

    public String getAccessToken() { return accessToken; }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }

    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public Long getExpiresIn() { return expiresIn; }

    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }

    public String getTokenType() { return tokenType; }

    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
}