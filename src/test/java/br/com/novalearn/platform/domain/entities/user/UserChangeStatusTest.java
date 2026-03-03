package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.core.exception.business.ForbiddenOperationException;
import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;
import static org.assertj.core.api.Assertions.*;

public class UserChangeStatusTest {
    private User user;

    @BeforeEach
    void setUp() { user = createInitializedUser(); }

    @Test
    void should_set_status_active_and_activate_user() {
        //when
        user.changeStatus(UserStatus.INACTIVE);

        //then
        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.isActive()).isFalse();
    }

    @Test
    void should_deactivate_user_when_status_is_not_active() {
        // When
        user.changeStatus(UserStatus.PENDING);

        // Then
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.isActive()).isFalse();
    }

    @Test
    void should_throw_exception_when_status_is_null() {
        // When / Then
        assertThatThrownBy(() -> user.changeStatus(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("User status is required");
    }

    @Test
    void should_throw_exception_when_changing_status_of_deleted_user() {
        // Given
        user.delete();

        // When / Then
        assertThatThrownBy(() -> user.changeStatus(UserStatus.ACTIVE))
                .isInstanceOf(ForbiddenOperationException.class);
    }
}