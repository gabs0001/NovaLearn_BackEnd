package br.com.novalearn.platform.api.controllers.course;

import br.com.novalearn.platform.api.dtos.course.CourseCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.course.CourseListResponseDTO;
import br.com.novalearn.platform.api.dtos.course.CourseResponseDTO;
import br.com.novalearn.platform.api.dtos.course.CourseUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.course.progress.CourseProgressResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleListResponseDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.course.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;
    private final AuthService authService;

    public CourseController(CourseService courseService, AuthService authService) {
        this.courseService = courseService;
        this.authService = authService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping
    public ResponseEntity<List<CourseListResponseDTO>> list() {
        List<CourseListResponseDTO> response = courseService.listAllActive();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> findById(@PathVariable Long id) {
        CourseResponseDTO response = courseService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CourseResponseDTO> create(@Valid @RequestBody CourseCreateRequestDTO request) {
        CourseResponseDTO response = courseService.create(getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CourseUpdateRequestDTO request
    ) {
        CourseResponseDTO response = courseService.update(id, request, getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        courseService.activate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        courseService.deactivate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        courseService.restore(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CourseResponseDTO> getBySlug(@PathVariable String slug) {
        CourseResponseDTO response = courseService.findBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}/modules")
    public ResponseEntity<List<ModuleListResponseDTO>> getModulesByCourse(@PathVariable String slug) {
        List<ModuleListResponseDTO> response = courseService.listByCourseSlug(slug);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<CourseProgressResponseDTO> getProgress(@PathVariable Long id) {
        CourseProgressResponseDTO response = courseService.getCourseProgress(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseListResponseDTO>> getByCategory(@PathVariable Long categoryId) {
        List<CourseListResponseDTO> response = courseService.listByCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<CourseListResponseDTO>> listAllAdmin() {
        List<CourseListResponseDTO> response = courseService.listAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-name")
    public ResponseEntity<Boolean> checkName(@RequestParam String name) {
        boolean exists = courseService.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/select")
    public ResponseEntity<List<CourseListResponseDTO>> select() {
        List<CourseListResponseDTO> response = courseService.listForSelect();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<CourseListResponseDTO>> featured() {
        List<CourseListResponseDTO> response = courseService.listFeatured();
        return ResponseEntity.ok(response);
    }
}