package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.api.dtos.user.UserUpdateRequestDTO;
import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.*;

public class UserUpdateProfileTest {
    private User user;

    @BeforeEach
    void setUp() { user = createInitializedUser(); }

    @Test
    void should_update_only_provided_fields() {
        //given
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setFirstName("New name");
        dto.setPhone("+1 (555) 456-7890");

        //when
        user.updateProfile(dto);

        //then
        assertThat(user.getFirstName()).isEqualTo("New name");
        assertThat(user.getPhone()).isEqualTo("+1 (555) 456-7890");
    }

    @Test
    void should_not_change_fields_that_are_not_provided() {
        //given
        String originalLastName = user.getLastName();
        String originalLocale = user.getLocale();

        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setFirstName("Name updated");

        //when
        user.updateProfile(dto);

        //then
        assertThat(user.getLastName()).isEqualTo(originalLastName);
        assertThat(user.getLocale()).isEqualTo(originalLocale);
    }

    @Test
    void should_update_cpf_when_valid_value_is_provided() {
        //given
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setCpf("12345678911");

        //when
        user.updateProfile(dto);

        //then
        assertThat(dto.getCpf()).isNotNull();
        assertThat(dto.getCpf()).isEqualTo("12345678911");
    }

    @Test
    void should_throw_exception_when_dto_is_null() {
        //when / then
        assertThatThrownBy(() -> user.updateProfile(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Update profile data is required");
    }

    @Test
    void should_throw_exception_when_updating_profile_of_deleted_user() {
        //given
        user.delete();

        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setFirstName("name");

        //when / then
        assertThatThrownBy(() -> user.updateProfile(dto))
                .isInstanceOf(ForbiddenOperationException.class);
    }
}