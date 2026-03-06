package br.com.novalearn.platform.api.controllers.review;

import br.com.novalearn.platform.api.dtos.review.ReviewCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewListResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewUpdateRequestDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.review.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final AuthService authService;
    private final ReviewService reviewService;

    public ReviewController(AuthService authService, ReviewService reviewService) {
        this.authService = authService;
        this.reviewService = reviewService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping
    public ResponseEntity<List<ReviewListResponseDTO>> list() {
        List<ReviewListResponseDTO> response = reviewService.listAllActive();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> findById(@PathVariable Long id) {
        ReviewResponseDTO response = reviewService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> create(@Valid @RequestBody ReviewCreateRequestDTO request) {
        ReviewResponseDTO response = reviewService.create(getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateRequestDTO request
    ) {
        ReviewResponseDTO response = reviewService.update(id, request, getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        reviewService.activate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        reviewService.deactivate(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Long id) {
        reviewService.restore(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ReviewResponseDTO>> getByCourse(@PathVariable Long courseId) {
        List<ReviewResponseDTO> response = reviewService.listByCourse(courseId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        reviewService.approve(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable Long id,
            @RequestParam(required = false) String observation
    ) {
        reviewService.reject(id, observation, getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<ReviewListResponseDTO>> listPending() {
        List<ReviewListResponseDTO> response = reviewService.listPendingApproval();
        return ResponseEntity.ok(response);
    }
}