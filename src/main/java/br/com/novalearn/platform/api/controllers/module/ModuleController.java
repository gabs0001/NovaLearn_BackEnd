package br.com.novalearn.platform.api.controllers.module;

import br.com.novalearn.platform.api.dtos.lesson.LessonListResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleListResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.module.progress.ModuleProgressResponseDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.module.ModuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {
    private final ModuleService moduleService;
    private final AuthService authService;

    public ModuleController(ModuleService moduleService, AuthService authService) {
        this.moduleService = moduleService;
        this.authService = authService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping
    public ResponseEntity<List<ModuleListResponseDTO>> list() {
        List<ModuleListResponseDTO> response = moduleService.listAllActive();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponseDTO> findById(@PathVariable Long id) {
        ModuleResponseDTO response = moduleService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ModuleResponseDTO> create(@Valid @RequestBody ModuleCreateRequestDTO request) {
        ModuleResponseDTO response = moduleService.create(getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ModuleResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ModuleUpdateRequestDTO request
    ) {
        ModuleResponseDTO response = moduleService.update(id, request, getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        moduleService.activate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        moduleService.deactivate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        moduleService.delete(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        moduleService.restore(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<ModuleProgressResponseDTO> getProgress(@PathVariable Long id) {
        ModuleProgressResponseDTO response = moduleService.getModuleProgress(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/lessons")
    public ResponseEntity<List<LessonListResponseDTO>> getByModule(@PathVariable Long id) {
        List<LessonListResponseDTO> response = moduleService.listByModule(id);
        return ResponseEntity.ok(response);
    }
}