package br.com.novalearn.platform.api.controllers.quiz;

import br.com.novalearn.platform.api.dtos.quiz.QuizCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.QuizUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.summary.QuizSummaryResponseDTO;
import br.com.novalearn.platform.api.dtos.user.quizattempt.UserQuizAttemptListResponseDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.quiz.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private final AuthService authService;
    private final QuizService quizService;

    public QuizController(AuthService authService, QuizService quizService) {
        this.authService = authService;
        this.quizService = quizService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping
    public ResponseEntity<List<QuizListResponseDTO>> list() {
        List<QuizListResponseDTO> response = quizService.listAllActive();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponseDTO> findById(@PathVariable Long id) {
        QuizResponseDTO response = quizService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<QuizResponseDTO> create(@Valid @RequestBody QuizCreateRequestDTO request) {
        QuizResponseDTO response = quizService.create(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<QuizResponseDTO> update(@PathVariable Long id, @Valid @RequestBody QuizUpdateRequestDTO request) {
        QuizResponseDTO response = quizService.update(id, request, getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        quizService.activate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        quizService.deactivate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        quizService.delete(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        quizService.restore(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<QuizResponseDTO>> getByModule(@PathVariable Long moduleId) {
        List<QuizResponseDTO> response = quizService.listByModule(moduleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{quizId}/summary")
    public ResponseEntity<QuizSummaryResponseDTO> getSummary(@PathVariable Long quizId) {
        QuizSummaryResponseDTO response = quizService.getSummary(quizId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{quizId}/attempts/me")
    public ResponseEntity<List<UserQuizAttemptListResponseDTO>> getMyAttempts(
            @PathVariable Long quizId
    ) {
        List<UserQuizAttemptListResponseDTO> response =
                quizService.listMyAttempts(quizId);

        return ResponseEntity.ok(response);
    }
}