package br.com.novalearn.platform.api.controllers.lesson;

import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.lesson.content.LessonContentResponseDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.lesson.LessonContentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lessons")
public class LessonContentController {
    private final LessonContentService lessonContentService;
    private final AuthService authService;

    public LessonContentController(LessonContentService lessonContentService, AuthService authService) {
        this.lessonContentService = lessonContentService;
        this.authService = authService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping("/{id}/content")
    public ResponseEntity<LessonContentResponseDTO> findByLesson(@PathVariable Long id) {
        LessonContentResponseDTO response = lessonContentService.findByLesson(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/content")
    public ResponseEntity<LessonContentResponseDTO> create(
            @PathVariable Long id,
            @Valid @RequestBody LessonContentCreateRequestDTO request
    ) {
        LessonContentResponseDTO response = lessonContentService.create(id, request, getUserId());
        return ResponseEntity.ok(response);
    }
}