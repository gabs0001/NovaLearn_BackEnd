package br.com.novalearn.platform.api.controllers.lesson;

import br.com.novalearn.platform.api.dtos.lesson.LessonCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonListResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonResponseDTO;
import br.com.novalearn.platform.api.dtos.lesson.LessonUpdateRequestDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.lesson.LessonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {
    private final AuthService authService;
    private final LessonService lessonService;

    public LessonController(AuthService authService, LessonService lessonService) {
        this.authService = authService;
        this.lessonService = lessonService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping
    public ResponseEntity<List<LessonListResponseDTO>> list() {
        List<LessonListResponseDTO> response = lessonService.listAllActive();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonResponseDTO> findById(@PathVariable Long id) {
        LessonResponseDTO response = lessonService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<LessonResponseDTO> create(@Valid @RequestBody LessonCreateRequestDTO request) {
        LessonResponseDTO response = lessonService.create(getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LessonResponseDTO> update(@PathVariable Long id, @Valid @RequestBody LessonUpdateRequestDTO request) {
        LessonResponseDTO response = lessonService.update(id, request, getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        lessonService.activate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        lessonService.deactivate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lessonService.delete(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        lessonService.restore(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<LessonListResponseDTO>> listByModule(@PathVariable Long moduleId) {
        List<LessonListResponseDTO> response = lessonService.listByModule(moduleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<LessonListResponseDTO>> listAllAdmin() {
        List<LessonListResponseDTO> response = lessonService.listAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-sequence")
    public ResponseEntity<Boolean> checkSequence(@RequestParam Long moduleId, @RequestParam Integer sequence) {
        boolean exists = lessonService.existsSequence(moduleId, sequence);
        return ResponseEntity.ok(exists);
    }

    @PatchMapping("/module/{moduleId}/reorder")
    public ResponseEntity<Void> reorderLessons(@PathVariable Long moduleId, @RequestBody List<Long> lessonIds) {
        lessonService.reorder(moduleId, lessonIds, getUserId());
        return ResponseEntity.noContent().build();
    }
}