package br.com.novalearn.platform.api.controllers.category;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.category.*;
import br.com.novalearn.platform.domain.services.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.dtos.category.CategoryTestFactory.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest extends BaseControllerTest {
    @MockitoBean
    private CategoryService categoryService;

    @BeforeEach
    void setUp() { mock_authenticated_user(); }

    @Test
    void should_return_categories_when_list() throws Exception {
        List<CategoryListResponseDTO> response = List.of(
                new CategoryListResponseDTO(
                        10L,
                        10L,
                        "Backend",
                        "BCK",
                        true,
                        false
                ),
                new CategoryListResponseDTO(
                        12L,
                        12L,
                        "Frontend",
                        "FRT",
                        true,
                        false
                )
        );

        when(categoryService.listAllActive()).thenReturn(response);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[1].id").value(12L))
                .andExpect(jsonPath("$[0].name").value("Backend"))
                .andExpect(jsonPath("$[1].name").value("Frontend"))
                .andExpect(jsonPath("$[0].abbreviation").value("BCK"))
                .andExpect(jsonPath("$[1].abbreviation").value("FRT"));

        verify(categoryService).listAllActive();
    }

    @Test
    void should_return_category_when_valid_id() throws Exception {
        CategoryResponseDTO response = categoryResponse();

        when(categoryService.findById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/categories/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Backend"))
                .andExpect(jsonPath("$.abbreviation").value("BCK"));

        verify(categoryService).findById(10L);
    }

    @Test
    void should_create_category_when_valid_request() throws Exception {
        CategoryCreateRequestDTO request = categoryCreateRequest();
        CategoryResponseDTO response = categoryResponse();

        when(categoryService.create(eq(USER_ID),
                any(CategoryCreateRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Backend"))
                .andExpect(jsonPath("$.abbreviation").value("BCK"));

        verify(categoryService).create(eq(USER_ID), any(CategoryCreateRequestDTO.class));
    }

    @Test
    void should_return_bad_request_when_create_with_invalid_data() throws Exception {
        CategoryCreateRequestDTO request = new CategoryCreateRequestDTO(
                null,
                "",
                "",
                "",
                ""
        );

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_update_category_when_valid_request() throws Exception {
        CategoryUpdateRequestDTO request = categoryUpdateRequest();

        CategoryResponseDTO response = new CategoryResponseDTO(
                10L,
                true,
                false,
                LocalDateTime.now().minusDays(5),
                null,
                "Observations",
                "Back-end",
                "Backend Development Category",
                "BE",
                10L
        );

        when(categoryService.update(eq(10L), any(CategoryUpdateRequestDTO.class), eq(USER_ID)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/categories/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Back-end"))
                .andExpect(jsonPath("$.description").value("Backend Development Category"))
                .andExpect(jsonPath("$.abbreviation").value("BE"));

        verify(categoryService).update(eq(10L), any(CategoryUpdateRequestDTO.class),
                        eq(USER_ID));
    }

    @Test
    void should_activate_category_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/categories/10/activate"))
                .andExpect(status().isNoContent());

        verify(categoryService).activate(10L, USER_ID);
    }

    @Test
    void should_deactivate_category_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/categories/10/deactivate"))
                .andExpect(status().isNoContent());

        verify(categoryService).deactivate(10L, USER_ID);
    }

    @Test
    void should_delete_category_when_valid_id() throws Exception {
        mockMvc.perform(delete("/api/categories/10"))
                .andExpect(status().isNoContent());

        verify(categoryService).delete(10L, USER_ID);
    }

    @Test
    void should_restore_category_when_valid_id() throws Exception {
        mockMvc.perform(patch("/api/categories/10/restore"))
                .andExpect(status().isNoContent());

        verify(categoryService).restore(10L, USER_ID);
    }

    @Test
    void should_return_all_categories_when_admin_list() throws Exception {
        List<CategoryListResponseDTO> response = List.of(
                new CategoryListResponseDTO(
                        10L,
                        10L,
                        "Backend",
                        "BCK",
                        true,
                        false
                ),
                new CategoryListResponseDTO(
                        12L,
                        12L,
                        "Frontend",
                        "FRT",
                        true,
                        false
                )
        );

        when(categoryService.listAll()).thenReturn(response);

        mockMvc.perform(get("/api/categories/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[1].id").value(12L))
                .andExpect(jsonPath("$[0].name").value("Backend"))
                .andExpect(jsonPath("$[1].name").value("Frontend"));

        verify(categoryService).listAll();
    }

    @Test
    void should_return_true_when_name_exists() throws Exception {
        when(categoryService.existsByName("Backend")).thenReturn(true);

        mockMvc.perform(get("/api/categories/check-name")
                        .param("name", "Backend"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(categoryService).existsByName("Backend");
    }

    @Test
    void should_return_categories_for_select() throws Exception {
        List<CategoryListResponseDTO> response = List.of(categoryListResponse());

        when(categoryService.listForSelect()).thenReturn(response);

        mockMvc.perform(get("/api/categories/select"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].name").value("Backend"));

        verify(categoryService).listForSelect();
    }
}