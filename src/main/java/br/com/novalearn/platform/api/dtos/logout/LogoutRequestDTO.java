package br.com.novalearn.platform.api.dtos.logout;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogoutRequestDTO {
    @NotBlank(message = "Refresh token is required.")
    private String refreshToken;

    protected LogoutRequestDTO() {}

    public LogoutRequestDTO(String refreshToken) { this.refreshToken = refreshToken; }

    public String getRefreshToken() { return refreshToken; }
}