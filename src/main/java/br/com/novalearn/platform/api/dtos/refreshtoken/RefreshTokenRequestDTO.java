package br.com.novalearn.platform.api.dtos.refreshtoken;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefreshTokenRequestDTO {
    @NotBlank(message = "Refresh token is required.")
    private String refreshToken;

    public RefreshTokenRequestDTO() {}

    public RefreshTokenRequestDTO(String refreshToken) { this.refreshToken = refreshToken; }

    public String getRefreshToken() { return refreshToken; }
}