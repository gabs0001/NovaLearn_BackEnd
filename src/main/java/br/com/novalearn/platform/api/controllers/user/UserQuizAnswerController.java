package br.com.novalearn.platform.api.controllers.user;

import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizanswer.UserQuizAnswerResponseDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.user.UserQuizAnswerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/quiz-answers")
public class UserQuizAnswerController {
    private final AuthService authService;
    private final UserQuizAnswerService userQuizAnswerService;

    public UserQuizAnswerController(AuthService authService, UserQuizAnswerService userQuizAnswerService) {
        this.authService = authService;
        this.userQuizAnswerService = userQuizAnswerService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @PostMapping
    public ResponseEntity<UserQuizAnswerResponseDTO> answerQuestion(
            @Valid @RequestBody UserQuizAnswerCreateRequestDTO request
    ) {
        UserQuizAnswerResponseDTO response = userQuizAnswerService.answerQuestion(request, getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/change-answer")
    public ResponseEntity<UserQuizAnswerResponseDTO> changeAnswer(
            @PathVariable Long id,
            @RequestParam Long optionId
    ) {
        UserQuizAnswerResponseDTO response = userQuizAnswerService.changeAnswer(id, optionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<UserQuizAnswerResponseDTO> findByQuestion(@PathVariable Long questionId) {
        UserQuizAnswerResponseDTO response = userQuizAnswerService.findMyAnswerByQuestion(questionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<UserQuizAnswerListResponseDTO>> listByQuiz(@PathVariable Long quizId) {
        List<UserQuizAnswerListResponseDTO> response = userQuizAnswerService.listMyAnswersByQuiz(quizId);
        return ResponseEntity.ok(response);
    }
}