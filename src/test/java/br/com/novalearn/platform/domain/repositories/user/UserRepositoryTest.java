package br.com.novalearn.platform.domain.repositories.user;

import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.enums.UserRole;
import br.com.novalearn.platform.domain.valueobjects.Cpf;
import br.com.novalearn.platform.domain.valueobjects.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User saveUser(String email, String cpf, boolean deleted) {
        Email emailVo = new Email(email);
        Cpf cpfVo = new Cpf(cpf);

        User user = new User(
                "John",
                "Doe",
                LocalDateTime.of(1993, 4, 10, 0, 0),
                cpfVo,
                "11999998888",
                emailVo,
                UserRole.ADMIN,
                "en-US",
                null,
                null,
                "Observations"
        );

        ReflectionTestUtils.setField(user, "passwordHash", "encoded-password-for-test");
        user.auditCreate(1L, LocalDateTime.now());

        user = userRepository.saveAndFlush(user);

        if(deleted) {
            ReflectionTestUtils.setField(user, "deleted", true);
            user = userRepository.saveAndFlush(user);
        }

        return user;
    }

    @Test
    void should_find_by_id_and_deleted_false() {
        User active = saveUser("a@test.com", "11111111111", false);
        User deleted = saveUser("b@test.com", "22222222222", true);

        entityManager.clear();

        Optional<User> foundActive = userRepository.findByIdAndDeletedFalse(active.getId());
        Optional<User> foundDeleted = userRepository.findByIdAndDeletedFalse(deleted.getId());

        assertThat(foundActive).isPresent();
        assertThat(foundDeleted).isEmpty();
    }

    @Test
    void should_find_by_email_and_deleted_false() {
        Email email = new Email("user@test.com");

        saveUser("user@test.com", "33333333333", false);
        saveUser("old@test.com", "44444444444", true);

        entityManager.clear();

        Optional<User> found = userRepository.findByEmailAndDeletedFalse(email);

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(email);
    }

    @Test
    void should_return_only_active_users_with_pagination() {
        saveUser("1@test.com", "11111111111", false);
        saveUser("2@test.com", "22222222222", false);
        saveUser("3@test.com", "33333333333", true);

        entityManager.clear();

        Page<User> page = userRepository.findAllByDeletedFalse(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void should_check_exists_by_email_and_deleted_false() {
        Email email = new Email("exists@test.com");

        saveUser("exists@test.com", "55555555555", false);

        entityManager.clear();

        boolean exists = userRepository.existsByEmailAndDeletedFalse(email);

        assertThat(exists).isTrue();
    }

    @Test
    void should_not_consider_deleted_when_checking_exists_by_email() {
        Email email = new Email("deleted@test.com");

        saveUser("deleted@test.com", "66666666666", true);

        entityManager.clear();

        boolean exists = userRepository.existsByEmailAndDeletedFalse(email);

        assertThat(exists).isFalse();
    }

    @Test
    void should_check_exists_by_email_and_id_not() {
        User u1 = saveUser("same@test.com", "77777777777", false);
        saveUser("other@test.com", "88888888888", false);

        entityManager.clear();

        boolean exists = userRepository.existsByEmailAndIdNotAndDeletedFalse(
                new Email("same@test.com"),
                u1.getId()
        );

        assertThat(exists).isFalse();
    }

    @Test
    void should_detect_conflict_email_for_other_user() {
        saveUser("conflict@test.com", "99999999999", false);
        User another = saveUser("other@test.com", "00000000000", false);

        entityManager.clear();

        boolean exists = userRepository.existsByEmailAndIdNotAndDeletedFalse(
                new Email("conflict@test.com"),
                another.getId()
        );

        assertThat(exists).isTrue();
    }
}