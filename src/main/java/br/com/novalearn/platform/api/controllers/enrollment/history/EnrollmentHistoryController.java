package br.com.novalearn.platform.api.controllers.enrollment.history;

import br.com.novalearn.platform.api.dtos.enrollment.history.EnrollmentHistoryResponseDTO;
import br.com.novalearn.platform.domain.services.enrollment.history.EnrollmentHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me/history")
public class EnrollmentHistoryController {
    private final EnrollmentHistoryService enrollmentHistoryService;

    public EnrollmentHistoryController(EnrollmentHistoryService enrollmentHistoryService) {
        this.enrollmentHistoryService = enrollmentHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentHistoryResponseDTO>> getAll() {
        List<EnrollmentHistoryResponseDTO> response = enrollmentHistoryService.getEnrollmentHistory();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<EnrollmentHistoryResponseDTO>> getCompleted() {
        List<EnrollmentHistoryResponseDTO> response = enrollmentHistoryService.getCompletedHistory();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/canceled")
    public ResponseEntity<List<EnrollmentHistoryResponseDTO>> getCanceled() {
        List<EnrollmentHistoryResponseDTO> response = enrollmentHistoryService.getCanceledHistory();
        return ResponseEntity.ok(response);
    }
}