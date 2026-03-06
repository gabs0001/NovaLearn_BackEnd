package br.com.novalearn.platform.api.controllers.category;

import br.com.novalearn.platform.api.dtos.category.CategoryCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.category.CategoryListResponseDTO;
import br.com.novalearn.platform.api.dtos.category.CategoryResponseDTO;
import br.com.novalearn.platform.api.dtos.category.CategoryUpdateRequestDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.category.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final AuthService authService;
    private final CategoryService categoryService;

    public CategoryController(AuthService authService, CategoryService categoryService) {
        this.authService = authService;
        this.categoryService = categoryService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping
    public ResponseEntity<List<CategoryListResponseDTO>> list() {
        List<CategoryListResponseDTO> response = categoryService.listAllActive();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findById(@PathVariable Long id) {
        CategoryResponseDTO response = categoryService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryCreateRequestDTO request) {
        CategoryResponseDTO response = categoryService.create(getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequestDTO request
    ) {
        CategoryResponseDTO response = categoryService.update(id, request, getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        categoryService.activate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        categoryService.deactivate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        categoryService.restore(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin")
    public ResponseEntity<List<CategoryListResponseDTO>> listAllAdmin() {
        List<CategoryListResponseDTO> response = categoryService.listAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-name")
    public ResponseEntity<Boolean> checkName(@RequestParam String name) {
        boolean exists = categoryService.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/select")
    public ResponseEntity<List<CategoryListResponseDTO>> select() {
        List<CategoryListResponseDTO> response = categoryService.listForSelect();
        return ResponseEntity.ok(response);
    }
}