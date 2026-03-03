package br.com.novalearn.platform.api.controllers.course;

import br.com.novalearn.platform.api.dtos.course.my.MyCourseResponseDTO;
import br.com.novalearn.platform.domain.services.course.MyCourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me/courses")
public class MyCourseController {
    private final MyCourseService myCourseService;

    public MyCourseController(MyCourseService myCourseService) { this.myCourseService = myCourseService; }

    @GetMapping
    public ResponseEntity<List<MyCourseResponseDTO>> getMyCourses() {
        List<MyCourseResponseDTO> response = myCourseService.listMyCourses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyCourseResponseDTO> getMyCourseById(@PathVariable Long id) {
        MyCourseResponseDTO response = myCourseService.getMyCourseById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<MyCourseResponseDTO>> getCompletedCourses() {
        List<MyCourseResponseDTO> response = myCourseService.listCompletedCourses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<MyCourseResponseDTO>> getCoursesInProgress() {
        List<MyCourseResponseDTO> response = myCourseService.listCoursesInProgress();
        return ResponseEntity.ok(response);
    }
}