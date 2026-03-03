package br.com.novalearn.platform.api.controllers.user;

import br.com.novalearn.platform.api.controllers.BaseControllerTest;
import br.com.novalearn.platform.api.dtos.password.ChangePasswordRequestDTO;
import br.com.novalearn.platform.api.dtos.user.*;
import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.enums.UserStatus;
import br.com.novalearn.platform.domain.services.user.UserService;
import br.com.novalearn.platform.domain.valueobjects.Cpf;
import br.com.novalearn.platform.domain.valueobjects.Email;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.novalearn.platform.factories.CommonTestFactory.*;

import static br.com.novalearn.platform.factories.dtos.user.UserTestFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest extends BaseControllerTest {
    @MockitoBean
    private UserService userService;

    @Test
    void should_return_page_when_list() throws Exception {
        when(userService.list(any())).thenReturn(new PageImpl<>(List.of(listResponse())));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].id").value(5L))
                .andExpect(jsonPath("$.content.[0].email").value("alex.rivers@example.com"))
                .andExpect(jsonPath("$.content.[0].role").value("ADMIN"));

        verify(userService).list(any());
    }

    @Test
    void should_return_user_when_find_by_id() throws Exception {
        when(userService.findById(5L)).thenReturn(response());

        mockMvc.perform(get("/api/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.email").value("alex.rivers@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(userService).findById(5L);
    }

    @Test
    void should_return_user_when_create() throws Exception {
        when(userService.create(any())).thenReturn(response());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Alex"));

        verify(userService).create(any());
    }

    @Test
    void should_return_400_when_invalid_create() throws Exception {
        UserCreateRequestDTO request = createInvalidRequest();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void should_use_authenticated_user_when_update() throws Exception {
        mock_authenticated_user();

        UserUpdateRequestDTO request = updateRequest();

        UserResponseDTO response = new UserResponseDTO(
                5L,
                true,
                false,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                "Premium user",
                "Alex",
                "Rivers",
                LocalDateTime.of(1992, 5, 15, 0, 0),
                "+1 (555) 456-7890",
                "12345678900",
                "alex.rivers@example.com",
                UserRole.ADMIN,
                UserStatus.ACTIVE,
                true,
                LocalDateTime.now().minusHours(2),
                "Senior Engineer",
                "https://api.dicebear.com/7.x/avataaars/svg?seed=Alex",
                "en-US",
                5L,
                null
        );

        when(userService.update(eq(5L), any(), eq(5L))).thenReturn(response);

        mockMvc.perform(patch("/api/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("+1 (555) 456-7890"));

        verify(userService).update(eq(5L), any(), eq(5L));
    }

    @Test
    void should_return_no_content_when_activate() throws Exception {
        mock_authenticated_user();

        doNothing().when(userService).activate(5L, 5L);

        mockMvc.perform(patch("/api/users/5/activate")).andExpect(status().isNoContent());

        verify(userService).activate(5L, 5L);
    }

    @Test
    void should_return_no_content_when_deactivate() throws Exception {
        mock_authenticated_user();

        doNothing().when(userService).deactivate(5L, 10L);

        mockMvc.perform(patch("/api/users/5/deactivate")).andExpect(status().isNoContent());

        verify(userService).deactivate(5L, 5L);
    }

    @Test
    void should_return_no_content_when_delete() throws Exception {
        mock_authenticated_user();

        doNothing().when(userService).delete(5L, 10L);

        mockMvc.perform(delete("/api/users/5")).andExpect(status().isNoContent());

        verify(userService).delete(5L, 5L);
    }

    @Test
    void should_return_no_content_when_restore() throws Exception {
        mock_authenticated_user();

        doNothing().when(userService).restore(5L, 5L);

        mockMvc.perform(patch("/api/users/5/restore")).andExpect(status().isNoContent());

        verify(userService).restore(5L, 5L);
    }

    @Test
    void should_return_no_content_when_change_my_password() throws Exception {
        mock_authenticated_user();

        ChangePasswordRequestDTO request = changePasswordRequest();

        doNothing().when(userService).changeMyPassword(eq(5L), any());

        mockMvc.perform(patch("/api/users/me/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService).changeMyPassword(eq(5L), any());
    }

    @Test
    void should_return_no_content_when_reset_password() throws Exception {
        mock_authenticated_user();

        doNothing().when(userService).resetPassword(5L, 5L);

        mockMvc.perform(patch("/api/users/5/reset-password")).andExpect(status().isNoContent());

        verify(userService).resetPassword(5L, 5L);
    }

    @Test
    void should_return_data_when_get_user_activity() throws Exception {
        when(userService.getUserActivity(5L)).thenReturn(activityResponse());

        mockMvc.perform(get("/api/users/5/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(5L))
                .andExpect(jsonPath("$.totalEnrollments").value(5))
                .andExpect(jsonPath("$.completedCourses").value(3));

        verify(userService).getUserActivity(5L);
    }
}