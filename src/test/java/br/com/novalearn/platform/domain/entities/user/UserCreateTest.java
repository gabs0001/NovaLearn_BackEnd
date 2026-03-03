package br.com.novalearn.platform.domain.entities.user;

import br.com.novalearn.platform.core.exception.business.ValidationException;
import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.enums.UserStatus;
import br.com.novalearn.platform.domain.events.user.UserRegisteredEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class UserCreateTest {
    @Test
    void should_register_user_with_default_state_and_event() {
        //given
        String firstName = "Alex";
        String lastName = "Rivers";
        String email = "alex.rivers@example.com";
        String cpf = "12345678900";
        String locale = "en-US";
        String phone = "+1 (555) 123-4567";
        String avatarUrl = "https://api.dicebear.com/7.x/avataaars/svg?seed=Alex";

        //when
        User user = User.register(
                firstName,
                lastName,
                email,
                cpf,
                locale,
                phone,
                avatarUrl
        );

        //then - basic attributes
        assertThat(user.getFirstName()).isEqualTo(firstName);
        assertThat(user.getLastName()).isEqualTo(lastName);
        assertThat(user.getEmail().toString()).isEqualTo(email);
        assertThat(user.getCpf().getValue()).isEqualTo(cpf);
        assertThat(user.getPhone()).isEqualTo(phone);
        assertThat(user.getLocale()).isEqualTo(locale);
        assertThat(user.getAvatarUrl()).isEqualTo(avatarUrl);

        //then - initial state
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.isEmailVerified()).isFalse();
        assertThat(user.isActive()).isTrue();
        assertThat(user.isDeleted()).isFalse();
        assertThat(user.getRole()).isEqualTo(UserRole.STUDENT);

        //then - domain event
        assertThat(user.getDomainEvents())
                .hasSize(1)
                .first()
                .isInstanceOf(UserRegisteredEvent.class);

        UserRegisteredEvent event = (UserRegisteredEvent) user.getDomainEvents().getFirst();

        assertThat(event.getEmail()).isEqualTo(email);
        assertThat(event.getOccurredAt()).isNotNull();
    }

    @Test
    void should_throw_exception_when_registering_user_without_email() {
        //given
        String firstName = "Alex";

        //when / then
        assertThatThrownBy(() ->
                User.register(
                        firstName,
                        null,
                        null,
                        null,
                        "pt-BR",
                        null,
                        null
                ))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void should_throw_exception_when_registering_user_with_invalid_email() {
        assertThatThrownBy(() ->
                User.register(
                        "Alex",
                        null,
                        "invalid-email",
                        null,
                        "en-US",
                        null,
                        null
                ))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void should_not_register_domain_event_when_user_registration_fails() {
        try {
            User.register(
                    "Alex",
                    null,
                    null,
                    null,
                    "en-US",
                    null,
                    null
            );
            fail("Expected ValidationException");
        }
        catch (ValidationException ex) {
            //ok
        }
    }
}