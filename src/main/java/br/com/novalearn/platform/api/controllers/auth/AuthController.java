package br.com.novalearn.platform.api.controllers.auth;

import br.com.novalearn.platform.api.dtos.login.LoginRequestDTO;
import br.com.novalearn.platform.api.dtos.login.LoginResponseDTO;
import br.com.novalearn.platform.api.dtos.logout.LogoutRequestDTO;
import br.com.novalearn.platform.api.dtos.password.ChangePasswordRequestDTO;
import br.com.novalearn.platform.api.dtos.refreshtoken.RefreshTokenRequestDTO;
import br.com.novalearn.platform.api.dtos.refreshtoken.RefreshTokenResponseDTO;
import br.com.novalearn.platform.api.dtos.register.RegisterRequestDTO;
import br.com.novalearn.platform.api.dtos.register.RegisterResponseDTO;
import br.com.novalearn.platform.api.dtos.user.UserResponseDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me() {
        UserResponseDTO response = authService.getAuthenticatedUser();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Void> check() {
        authService.checkAuthentication();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        RegisterResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        RefreshTokenResponseDTO response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequestDTO request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll() {
        authService.logoutAll();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        authService.changePassword(request);
        return ResponseEntity.noContent().build();
    }
}