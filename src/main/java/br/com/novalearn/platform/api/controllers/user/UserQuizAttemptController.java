package br.com.novalearn.platform.api.controllers.user;

import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptUpdateRequestDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.user.UserQuizAttemptService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/quiz-attempts")
public class UserQuizAttemptController {
    private final AuthService authService;
    private final UserQuizAttemptService userQuizAttemptService;

    public UserQuizAttemptController(
            AuthService authService,
            UserQuizAttemptService userQuizAttemptService
    ) {
        this.authService = authService;
        this.userQuizAttemptService = userQuizAttemptService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping
    public ResponseEntity<List<UserQuizAttemptListResponseDTO>> listMyAttempts() {
        List<UserQuizAttemptListResponseDTO> response = userQuizAttemptService.listMyAttempts(getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserQuizAttemptResponseDTO> getMyAttempt(@PathVariable Long id) {
        UserQuizAttemptResponseDTO response = userQuizAttemptService.findMyAttemptById(id, getUserId());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserQuizAttemptResponseDTO> startAttempt(
            @Valid @RequestBody UserQuizAttemptCreateRequestDTO request
    ) {
        UserQuizAttemptResponseDTO response = userQuizAttemptService.startAttempt(request, getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/finish")
    public ResponseEntity<UserQuizAttemptResponseDTO> finishAttempt(
            @PathVariable Long id,
            @Valid @RequestBody UserQuizAttemptUpdateRequestDTO request
    ) {
        UserQuizAttemptResponseDTO response = userQuizAttemptService
                .finishAttempt(id, request, getUserId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userQuizAttemptService.deleteMyAttempt(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/quiz/{quizId}/me")
    public ResponseEntity<List<UserQuizAttemptListResponseDTO>> listMyAttemptsByQuiz(
            @PathVariable Long quizId
    ) {
        List<UserQuizAttemptListResponseDTO> response =
                userQuizAttemptService.listMyAttemptsByQuiz(quizId, getUserId());

        return ResponseEntity.ok(response);
    }
}