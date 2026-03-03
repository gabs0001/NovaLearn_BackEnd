package br.com.novalearn.platform.api.controllers.review;

import br.com.novalearn.platform.api.dtos.review.ReviewListResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewResponseDTO;
import br.com.novalearn.platform.api.dtos.review.ReviewUpdateRequestDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.review.MyReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/reviews")
public class MyReviewController {
    private final AuthService authService;
    private final MyReviewService myReviewService;

    public MyReviewController(AuthService authService, MyReviewService myReviewService) {
        this.authService = authService;
        this.myReviewService = myReviewService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping
    public ResponseEntity<List<ReviewListResponseDTO>> listMyReviews() {
        List<ReviewListResponseDTO> response = myReviewService.listMyReviews(getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getMyReviewById(@PathVariable Long id) {
        ReviewResponseDTO response = myReviewService.findMyReviewById(id, getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateMyReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewUpdateRequestDTO request
    ) {
        ReviewResponseDTO response = myReviewService.updateMyReview(id, request, getUserId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyReview(@PathVariable Long id) {
        myReviewService.deleteMyReview(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restoreMyReview(@PathVariable Long id) {
        myReviewService.restoreMyReview(id, getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ReviewResponseDTO> getMyReviewByCourse(@PathVariable Long courseId) {
        ReviewResponseDTO response = myReviewService.findMyReviewByCourse(courseId, getUserId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/mark-anonymous")
    public ResponseEntity<Void> markAnonymous(@PathVariable Long id) {
        myReviewService.markAnonymous(id, getUserId());
        return ResponseEntity.noContent().build();
    }
}