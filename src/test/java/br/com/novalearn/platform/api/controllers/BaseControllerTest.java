package br.com.novalearn.platform.api.controllers;

import br.com.novalearn.platform.core.security.*;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected JWTUtil jwtUtil;

    @MockitoBean
    protected JWTAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    protected CustomAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    protected CustomAccessDeniedHandler accessDeniedHandler;

    protected static final Long USER_ID = 5L;

    protected void mock_authenticated_user() {
        when(authService.getAuthenticatedUserId())
                .thenReturn(USER_ID);
    }
}