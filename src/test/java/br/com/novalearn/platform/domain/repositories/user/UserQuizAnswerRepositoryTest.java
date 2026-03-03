package br.com.novalearn.platform.domain.repositories.user;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.User;
import br.com.novalearn.platform.domain.entities.user.UserQuizAnswer;
import br.com.novalearn.platform.domain.repositories.quiz.QuizAnswerOptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserQuizAnswerRepositoryTest {
    @Autowired
    private UserQuizAnswerRepository userQuizAnswerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    private User user;
    private User otherUser;

    private Category category;
    private Course course;
    private Module module;
    private Quiz quiz;
    private QuizQuestion question1;
    private QuizQuestion question2;

    private QuizAnswerOption option1;
    private QuizAnswerOption option2;

    private UserQuizAnswer answer1;
    private UserQuizAnswer answer2;
    private UserQuizAnswer otherUserAnswer;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        Long actorId = 1L;

        user = createUser("a@test.com", "11111111111");
        otherUser = createUser("b@test.com", "22222222222");

        ReflectionTestUtils.setField(user, "createdBy", actorId);
        ReflectionTestUtils.setField(user, "createdAt", now);
        ReflectionTestUtils.setField(user, "active", true);
        ReflectionTestUtils.setField(user, "deleted", false);

        ReflectionTestUtils.setField(otherUser, "createdBy", actorId);
        ReflectionTestUtils.setField(otherUser, "createdAt", now);
        ReflectionTestUtils.setField(otherUser, "active", true);
        ReflectionTestUtils.setField(otherUser, "deleted", false);

        user = entityManager.persistAndFlush(user);
        otherUser = entityManager.persistAndFlush(otherUser);

        category = createCategory("Backend");
        category.auditCreate(user.getId(), now);
        category = entityManager.persistAndFlush(category);

        course = createCourse("Java", category, user);
        course.auditCreate(user.getId(), now);
        course = entityManager.persistAndFlush(course);

        module = createModule("Fundamentals", course, 1);
        module.auditCreate(user.getId(), now);
        module = entityManager.persistAndFlush(module);

        quiz = createQuiz("Quiz Java", module);
        quiz.auditCreate(user.getId(), now);
        quiz = entityManager.persistAndFlush(quiz);

        question1 = createQuestion("What is JVM?", quiz, 1);
        question1.auditCreate(user.getId(), now);
        question1 = entityManager.persistAndFlush(question1);

        question2 = createQuestion("What is JDK?", quiz, 2);
        question2.auditCreate(user.getId(), now);
        question2 = entityManager.persistAndFlush(question2);

        option1 = createOption("Java Virtual Machine", question1, 1, true);
        option1.auditCreate(user.getId(), now);
        option1 = entityManager.persistAndFlush(option1);

        option2 = createOption("Java Development Kit", question2, 1, true);
        option2.auditCreate(user.getId(), now);
        option2 = entityManager.persistAndFlush(option2);

        answer1 = createAnswer(user, question1, option1);
        answer1.auditCreate(user.getId(), now);

        answer2 = createAnswer(user, question2, option2);
        answer2.auditCreate(user.getId(), now);

        otherUserAnswer = createAnswer(otherUser, question1, option1);
        otherUserAnswer.auditCreate(otherUser.getId(), now);

        persistAnswers();
    }

    private void persistAnswers() {
        entityManager.persist(answer1);
        entityManager.persist(answer2);
        entityManager.persist(otherUserAnswer);
        entityManager.flush();
    }

    private User createUser(String email, String cpf) {
        User user = User.register(
                "João",
                "Silva",
                email,
                cpf,
                "pt-BR",
                null,
                null
        );

        user.initializeNewUser("encoded", now);

        return user;
    }

    private Category createCategory(String name) {
        return Category.create(
                name,
                "BCK",
                "Some Description",
                "Observations"
        );
    }

    private Course createCourse(
            String name,
            Category category,
            User user
    ) {
        return Course.create(name, category, user);
    }

    private Module createModule(String name, Course course, int sequence) {
        return Module.create(
                course,
                name,
                "description",
                sequence,
                "observations"
        );
    }

    private Quiz createQuiz(String name, Module module) {
        return Quiz.create(
                module,
                name,
                10,
                new BigDecimal("70"),
                3,
                true
        );
    }

    private QuizQuestion createQuestion(
            String title,
            Quiz quiz,
            int sequence
    ) {
        return QuizQuestion.create(
                quiz,
                sequence,
                title,
                5
        );
    }

    private QuizAnswerOption createOption(
            String text,
            QuizQuestion question,
            int sequence,
            boolean correct
    ) {

        QuizAnswerOption option = QuizAnswerOption.create(
                question,
                sequence,
                text,
                false,
                null,
                1L,
                now
        );

        option.setCreatedAt(now);

        if(correct) option.changeCorrect(true);

        return option;
    }

    private UserQuizAnswer createAnswer(
            User user,
            QuizQuestion question,
            QuizAnswerOption option
    ) {
        return UserQuizAnswer.answer(
                user,
                question,
                option,
                now,
                now
        );
    }

    private UserQuizAnswer createDeletedAnswer(
            User user,
            QuizQuestion question,
            QuizAnswerOption option
    ) {
        UserQuizAnswer answer = UserQuizAnswer.answer(
                user,
                question,
                option,
                now,
                now
        );

        answer.auditCreate(user.getId(), now);
        answer.delete();

        return answer;
    }

    @Test
    void should_find_all_not_deleted() {
        UserQuizAnswer deleted = createDeletedAnswer(user, question1, option1);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        List<UserQuizAnswer> result = userQuizAnswerRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(3);
    }

    @Test
    void should_find_user_and_question_ids() {
        List<Long> questionIds = List.of(
                question1.getId(),
                question2.getId()
        );

        List<UserQuizAnswer> result = userQuizAnswerRepository
                .findAllByUserIdAndQuizQuestionIdInAndDeletedFalse(user.getId(), questionIds);

        assertThat(result).hasSize(2);

        assertThat(result).allMatch(a -> a.getUser().getId().equals(user.getId()));
    }

    @Test
    void should_find_by_user_and_quiz() {
        List<UserQuizAnswer> result = userQuizAnswerRepository
                .findAllByUserIdAndQuizQuestionQuizIdAndDeletedFalse(user.getId(), quiz.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void should_find_by_user_and_question() {
        Optional<UserQuizAnswer> result = userQuizAnswerRepository
                .findByUserIdAndQuizQuestionIdAndDeletedFalse(user.getId(), question1.getId());

        assertThat(result).isPresent();

        assertThat(result.get().getQuizQuestion().getId()).isEqualTo(question1.getId());
    }

    @Test
    void should_not_find_deleted_answer() {
        entityManager.remove(answer2);
        entityManager.flush();

        UserQuizAnswer deleted = UserQuizAnswer.answer(
                user,
                question2,
                option2,
                now,
                now
        );

        deleted.auditCreate(user.getId(), now);
        deleted.delete();

        entityManager.persist(deleted);
        entityManager.flush();
        entityManager.clear();

        Optional<UserQuizAnswer> result = userQuizAnswerRepository
                .findByUserIdAndQuizQuestionIdAndDeletedFalse(user.getId(), question2.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void should_find_by_id_and_user() {
        Optional<UserQuizAnswer> result = userQuizAnswerRepository
                .findByIdAndUserIdAndDeletedFalse(answer1.getId(), user.getId());

        assertThat(result).isPresent();
    }

    @Test
    void should_not_find_other_user_answer_by_id() {
        Optional<UserQuizAnswer> result = userQuizAnswerRepository
                .findByIdAndUserIdAndDeletedFalse(otherUserAnswer.getId(), user.getId());

        assertThat(result).isEmpty();
    }
}