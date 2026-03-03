package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.*;

public class UserChangeRoleTest {
    private User user;

    @BeforeEach
    void setUp() { user = createInitializedUser(); }

    @Test
    void should_change_user_role() {
        //when
        user.changeRole(UserRole.ADMIN);

        //then
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void should_throw_exception_when_role_is_null() {
        // When / Then
        assertThatThrownBy(() -> user.changeRole(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("User role is required");
    }

    @Test
    void should_throw_exception_when_changing_role_of_deleted_user() {
        // Given
        user.delete();

        // When / Then
        assertThatThrownBy(() -> user.changeRole(UserRole.ADMIN))
                .isInstanceOf(ForbiddenOperationException.class);
    }
}