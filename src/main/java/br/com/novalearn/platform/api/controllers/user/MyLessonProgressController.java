package br.com.novalearn.platform.api.controllers.user;

import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressListResponseDTO;
import br.com.novalearn.platform.api.dtos.user.lessonprogress.UserLessonProgressResponseDTO;
import br.com.novalearn.platform.domain.services.auth.AuthService;
import br.com.novalearn.platform.domain.services.user.MyLessonProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/lessons")
public class MyLessonProgressController {
    private final AuthService authService;
    private final MyLessonProgressService myLessonProgressService;

    public MyLessonProgressController(
            AuthService authService,
            MyLessonProgressService myLessonProgressService
    ) {
        this.authService = authService;
        this.myLessonProgressService = myLessonProgressService;
    }

    private Long getUserId() { return authService.getAuthenticatedUserId(); }

    @GetMapping("/progress")
    public ResponseEntity<List<UserLessonProgressListResponseDTO>> listMyProgress() {
        return ResponseEntity.ok(
                myLessonProgressService.listMyProgress(getUserId())
        );
    }

    @PatchMapping("/{lessonId}/complete")
    public ResponseEntity<UserLessonProgressResponseDTO> completeLesson(
            @PathVariable Long lessonId
    ) {
        return ResponseEntity.ok(
                myLessonProgressService.completeLesson(lessonId, getUserId())
        );
    }
}