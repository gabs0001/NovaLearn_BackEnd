package br.com.novalearn.platform.api.controllers.auth;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.login.LoginRequestDTO;
import br.com.novalearn.platform.api.dtos.logout.LogoutRequestDTO;
import br.com.novalearn.platform.api.dtos.password.ChangePasswordRequestDTO;
import br.com.novalearn.platform.api.dtos.user.UserResponseDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.novalearn.platform.factories.dtos.auth.AuthTestFactory.*;
import static br.com.novalearn.platform.factories.CommonTestFactory.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest extends BaseControllerTest {
    @Test
    void me_should_return_authenticated_user() throws Exception {
        UserResponseDTO response = authenticatedUser();
        when(authService.getAuthenticatedUser()).thenReturn(response);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.email").value("alex.rivers@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(authService).getAuthenticatedUser();
    }

    @Test
    void check_should_return_no_content() throws Exception {
        doNothing().when(authService).checkAuthentication();

        mockMvc.perform(get("/api/auth/check")).andExpect(status().isNoContent());

        verify(authService).checkAuthentication();
    }

    @Test
    void login_should_return_token() throws Exception {
        when(authService.login(any())).thenReturn(loginResponse());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

        verify(authService).login(any());
    }

    @Test
    void login_should_return_400_when_invalid_request() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("", "", null);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void register_should_return_ok() throws Exception {
        when(authService.register(any())).thenReturn(registerResponse());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.email").value("alex.rivers@example.com"));

        verify(authService).register(any());
    }

    @Test
    void refresh_should_return_new_token() throws Exception {
        when(authService.refreshToken(any())).thenReturn(refreshResponse());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.mustReauthenticate").value(true));

        verify(authService).refreshToken(any());
    }

    @Test
    void logout_should_return_no_content() throws Exception {
        LogoutRequestDTO request = logoutRequest();

        doNothing().when(authService).logout(any());

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(authService).logout(any());
    }

    @Test
    void logout_all_should_return_no_content() throws Exception {
        doNothing().when(authService).logoutAll();

        mockMvc.perform(post("/api/auth/logout-all")).andExpect(status().isNoContent());

        verify(authService).logoutAll();
    }

    @Test
    void change_password_should_return_no_content() throws Exception {
        ChangePasswordRequestDTO request = changePasswordRequest();

        doNothing().when(authService).changePassword(any());

        mockMvc.perform(patch("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(authService).changePassword(any());
    }
}