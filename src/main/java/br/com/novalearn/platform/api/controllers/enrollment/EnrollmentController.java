package br.com.novalearn.platform.api.controllers.enrollment;

import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentRequestDTO;
import br.com.novalearn.platform.api.dtos.enrollment.EnrollmentResponseDTO;
import br.com.novalearn.platform.api.dtos.enrollment.UpdateProgressRequestDTO;
import br.com.novalearn.platform.domain.services.enrollment.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) { this.enrollmentService = enrollmentService; }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponseDTO>> list() {
        List<EnrollmentResponseDTO> response = enrollmentService.listMyEnrollments();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<EnrollmentResponseDTO> getDetails(@PathVariable Long courseId) {
        EnrollmentResponseDTO response = enrollmentService.getEnrollmentDetails(courseId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> enroll(@Valid @RequestBody EnrollmentRequestDTO request) {
        EnrollmentResponseDTO response = enrollmentService.enroll(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{courseId}/progress")
    public ResponseEntity<EnrollmentResponseDTO> updateProgress(@PathVariable Long courseId, @Valid @RequestBody UpdateProgressRequestDTO request) {
        EnrollmentResponseDTO response = enrollmentService.updateProgress(courseId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/certificate")
    public ResponseEntity<EnrollmentResponseDTO> issueCertificate(@PathVariable Long courseId) {
        EnrollmentResponseDTO response = enrollmentService.issueCertificate(courseId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<EnrollmentResponseDTO> cancelEnrollment(@PathVariable Long courseId) {
        EnrollmentResponseDTO response = enrollmentService.cancelEnrollment(courseId);
        return ResponseEntity.ok(response);
    }
}