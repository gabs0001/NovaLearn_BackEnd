package br.com.novalearn.platform.api.dtos.refreshtoken;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefreshTokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType;
    private Boolean mustReauthenticate;

    public RefreshTokenResponseDTO() {}

    public RefreshTokenResponseDTO(String accessToken, String refreshToken, Long expiresIn, String tokenType, Boolean mustReauthenticate) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.mustReauthenticate = mustReauthenticate;
    }

    public String getAccessToken() { return accessToken; }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }

    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public Long getExpiresIn() { return expiresIn; }

    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }

    public String getTokenType() { return tokenType; }

    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Boolean getMustReauthenticate() { return mustReauthenticate; }

    public void setMustReauthenticate(Boolean mustReauthenticate) { this.mustReauthenticate = mustReauthenticate; }
}