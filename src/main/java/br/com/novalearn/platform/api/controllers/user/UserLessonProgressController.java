package br.com.novalearn.platform.api.controllers.user;

import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressResponseDTO;
import br.com.novalearn.platform.domain.services.user.UserLessonProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/lessons")
public class UserLessonProgressController {
    private final UserLessonProgressService userLessonProgressService;

    public UserLessonProgressController(UserLessonProgressService userLessonProgressService) {
        this.userLessonProgressService = userLessonProgressService;
    }

    @GetMapping
    public ResponseEntity<List<UserLessonProgressListResponseDTO>> list() {
        List<UserLessonProgressListResponseDTO> response = userLessonProgressService.listAllActive();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserLessonProgressResponseDTO> findById(@PathVariable Long id) {
        UserLessonProgressResponseDTO response = userLessonProgressService.findById(id);
        return ResponseEntity.ok(response);
    }
}