package br.com.novalearn.platform.api.controllers.module;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.lesson.LessonListResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleCreateRequestDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleListResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleResponseDTO;
import br.com.novalearn.platform.api.dtos.module.ModuleUpdateRequestDTO;
import br.com.novalearn.platform.api.dtos.module.progress.ModuleProgressResponseDTO;
import br.com.novalearn.platform.domain.services.module.ModuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.dtos.module.ModuleTestFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModuleController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class ModuleControllerTest extends BaseControllerTest {
    @MockitoBean
    private ModuleService moduleService;

    @BeforeEach
    void setUp() { mock_authenticated_user(); }

    @Test
    void should_return_module_list_when_list_all_active() throws Exception {
        List<ModuleListResponseDTO> response = List.of(
                new ModuleListResponseDTO(
                        6L,
                        1L,
                        "Backend essentials",
                        1,
                        true,
                        false
                ),
                new ModuleListResponseDTO(
                        2L,
                        1L,
                        "Frontend essentials",
                        1,
                        true,
                        false
                )
        );

        when(moduleService.listAllActive()).thenReturn(response);

        mockMvc.perform(get("/api/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(6L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[0].name").value("Backend essentials"))
                .andExpect(jsonPath("$[1].name").value("Frontend essentials"));

        verify(moduleService).listAllActive();
    }

    @Test
    void should_return_module_when_find_by_id() throws Exception {
        ModuleResponseDTO response = moduleResponse();

        when(moduleService.findById(6L)).thenReturn(response);

        mockMvc.perform(get("/api/modules/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.name").value("Backend essentials"));

        verify(moduleService).findById(6L);
    }

    @Test
    void should_create_module_when_valid_request() throws Exception {
        ModuleCreateRequestDTO request = moduleCreateRequest();
        ModuleResponseDTO response = moduleResponse();

        when(moduleService.create(eq(USER_ID), any(ModuleCreateRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.courseId").value(1L))
                .andExpect(jsonPath("$.name").value("Backend essentials"));

        verify(moduleService).create(eq(USER_ID), any(ModuleCreateRequestDTO.class));
    }

    @Test
    void should_return_bad_request_when_create_with_invalid_data() throws Exception {
        ModuleCreateRequestDTO request = new ModuleCreateRequestDTO(
                null,
                "",
                "",
                0,
                ""
        );

        mockMvc.perform(post("/api/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_update_module_when_valid_request() throws Exception {
        ModuleUpdateRequestDTO request = moduleUpdateRequest();

        ModuleResponseDTO response = new ModuleResponseDTO(
                6L,
                true,
                false,
                LocalDateTime.now().minusDays(2),
                null,
                "Observations",
                1L,
                "Backend base",
                "Some new module description",
                1,
                5L,
                null
        );

        when(moduleService.update(eq(6L), any(ModuleUpdateRequestDTO.class), eq(USER_ID)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/modules/6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Backend base"))
                .andExpect(jsonPath("$.description").value("Some new module description"));

        verify(moduleService)
                .update(eq(6L), any(ModuleUpdateRequestDTO.class), eq(USER_ID));
    }

    @Test
    void should_activate_module_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/modules/6/activate"))
                .andExpect(status().isNoContent());

        verify(moduleService).activate(6L, USER_ID);
    }

    @Test
    void should_deactivate_module_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/modules/6/deactivate"))
                .andExpect(status().isNoContent());

        verify(moduleService).deactivate(6L, USER_ID);
    }

    @Test
    void should_delete_module_when_valid_id() throws Exception {
        mockMvc.perform(delete("/api/modules/6"))
                .andExpect(status().isNoContent());

        verify(moduleService).delete(6L, USER_ID);
    }

    @Test
    void should_restore_module_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/modules/6/restore"))
                .andExpect(status().isNoContent());

        verify(moduleService).restore(6L, USER_ID);
    }

    @Test
    void should_return_progress_when_get_module_progress() throws Exception {
        ModuleProgressResponseDTO response = moduleProgressResponse();

        when(moduleService.getModuleProgress(6L)).thenReturn(response);

        mockMvc.perform(get("/api/modules/6/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moduleId").value(6L))
                .andExpect(jsonPath("$.courseId").value(1L))
                .andExpect(jsonPath("$.name").value("Backend essentials"))
                .andExpect(jsonPath("$.progressPercent").value(50));

        verify(moduleService).getModuleProgress(6L);
    }

    @Test
    void should_return_lessons_when_get_by_module() throws Exception {
        List<LessonListResponseDTO> response = List.of(
                new LessonListResponseDTO(
                        3L,
                        6L,
                        "Java introduction",
                        1,
                        300,
                        true,
                        true
                ),
                new LessonListResponseDTO(
                        4L,
                        6L,
                        "Java basics",
                        1,
                        400,
                        true,
                        true
                )
        );

        when(moduleService.listByModule(6L)).thenReturn(response);

        mockMvc.perform(get("/api/modules/6/lessons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[1].id").value(4L))
                .andExpect(jsonPath("$[0].name").value("Java introduction"))
                .andExpect(jsonPath("$[1].name").value("Java basics"))
                .andExpect(jsonPath("$[0].durationSeconds").value(300))
                .andExpect(jsonPath("$[1].durationSeconds").value(400));

        verify(moduleService).listByModule(6L);
    }
}