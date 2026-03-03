package br.com.novalearn.platform.api.controllers.quiz;

import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionListResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.answeroption.QuizAnswerOptionUpdateRequestDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.quiz.QuizAnswerOptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes/{quizId}/questions/{questionId}/options")
public class QuizAnswerOptionController {
    private final AuthService authService;
    private final QuizAnswerOptionService quizAnswerOptionService;

    public QuizAnswerOptionController(AuthService authService, QuizAnswerOptionService quizAnswerOptionService) {
        this.authService = authService;
        this.quizAnswerOptionService = quizAnswerOptionService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping
    public ResponseEntity<List<QuizAnswerOptionResponseDTO>> listByQuestion(@PathVariable Long questionId) {
        List<QuizAnswerOptionResponseDTO> response = quizAnswerOptionService.listByQuestion(questionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizAnswerOptionResponseDTO> findById(@PathVariable Long id) {
        QuizAnswerOptionResponseDTO response = quizAnswerOptionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<QuizAnswerOptionResponseDTO> create(@Valid @RequestBody QuizAnswerOptionCreateRequestDTO request) {
        QuizAnswerOptionResponseDTO response = quizAnswerOptionService.create(getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<QuizAnswerOptionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody QuizAnswerOptionUpdateRequestDTO request
    ) {
        QuizAnswerOptionResponseDTO response = quizAnswerOptionService.update(id, request, getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        quizAnswerOptionService.activate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        quizAnswerOptionService.deactivate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        quizAnswerOptionService.delete(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        quizAnswerOptionService.restore(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/mark-correct")
    public ResponseEntity<Void> markAsCorrect(@PathVariable Long id) {
        quizAnswerOptionService.markAsCorrect(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody List<Long> orderedIds) {
        quizAnswerOptionService.reorder(orderedIds, getUserId());
        return ResponseEntity.noContent().build();
    }
}