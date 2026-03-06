package br.com.novalearn.platform.api.controllers.quiz;

import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionResponseDTO;
import br.com.novalearn.platform.api.dtos.quiz.question.QuizQuestionUpdateRequestDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.quiz.QuizQuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes/{quizId}/questions")
public class QuizQuestionController {
    private final AuthService authService;
    private final QuizQuestionService quizQuestionService;

    public QuizQuestionController(AuthService authService, QuizQuestionService quizQuestionService) {
        this.authService = authService;
        this.quizQuestionService = quizQuestionService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping
    public ResponseEntity<List<QuizQuestionResponseDTO>> listByQuiz(@PathVariable Long quizId) {
        List<QuizQuestionResponseDTO> response = quizQuestionService.listByQuiz(quizId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizQuestionResponseDTO> findById(@PathVariable Long id) {
        QuizQuestionResponseDTO response = quizQuestionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<QuizQuestionResponseDTO> create(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizQuestionCreateRequestDTO request
    ) {
        request.setQuizId(quizId);
        QuizQuestionResponseDTO response = quizQuestionService.create(getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<QuizQuestionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody QuizQuestionUpdateRequestDTO request
    ) {
        QuizQuestionResponseDTO response = quizQuestionService.update(id, request, getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        quizQuestionService.activate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        quizQuestionService.deactivate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        quizQuestionService.delete(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        quizQuestionService.restore(id, getUserId());
        return ResponseEntity.noContent().build();
    }
}