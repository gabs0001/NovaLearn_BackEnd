package br.com.novalearn.platform.domain.repositories.quiz;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
import br.com.novalearn.platform.domain.entities.quiz.QuizAnswerOption;
import br.com.novalearn.platform.domain.entities.quiz.QuizQuestion;
import br.com.novalearn.platform.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class QuizAnswerOptionRepositoryTest {
    @Autowired
    private QuizAnswerOptionRepository quizAnswerOptionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    private User user;
    private Category category;
    private Course course;
    private Module module;
    private Quiz quiz;
    private QuizQuestion question;

    private QuizAnswerOption option1;
    private QuizAnswerOption option2;
    private QuizAnswerOption correctOption;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        user = createUser("a@test.com", "11111111111");

        category = createCategory("Backend");
        category.setCreatedAt(now);

        course = createCourse("Java", category, user);
        course.setCreatedAt(now);

        module = createModule("Fundamentals", course, 1);
        module.setCreatedAt(now);

        quiz = createQuiz("Quiz Java", module);
        quiz.setCreatedAt(now);

        question = createQuestion("What is JVM?", quiz, 1);
        question.setCreatedAt(now);

        option1 = createOption("Java Virtual Machine", question, 1, false);
        option1.setCreatedAt(now);

        option2 = createOption("Java Version Manager", question, 2, false);
        option2.setCreatedAt(now);

        correctOption = createOption("Java Virtual Machine", question, 3, true);
        correctOption.setCreatedAt(now);

        persistAll();
    }

    private void persistAll() {
        entityManager.persist(user);
        entityManager.persist(category);
        entityManager.persist(course);
        entityManager.persist(module);
        entityManager.persist(quiz);
        entityManager.persist(question);

        entityManager.persist(option1);
        entityManager.persist(option2);
        entityManager.persist(correctOption);
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
                5L,
                now
        );

        option.setCreatedAt(now);

        if(correct) option.changeCorrect(true);

        return option;
    }

    private QuizAnswerOption createDeletedOption(
            String text,
            QuizQuestion question,
            int sequence
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
        option.delete();

        return option;
    }

    @Test
    void should_find_all_not_deleted() {
        QuizAnswerOption deleted = createDeletedOption(
                "Option removed",
                question,
                4
        );

        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        List<QuizAnswerOption> result = quizAnswerOptionRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(3);
    }

    @Test
    void should_find_all_by_question_not_deleted() {
        List<QuizAnswerOption> result = quizAnswerOptionRepository
                        .findAllByQuizQuestionIdAndDeletedFalse(question.getId());

        assertThat(result).hasSize(3);
    }

    @Test
    void should_find_all_by_question_ordered_by_sequence() {
        List<QuizAnswerOption> result = quizAnswerOptionRepository
                .findAllByQuizQuestionIdAndDeletedFalseOrderBySequenceAsc(question.getId());

        assertThat(result).hasSize(3);

        assertThat(result.get(0).getSequence()).isEqualTo(1);
        assertThat(result.get(1).getSequence()).isEqualTo(2);
        assertThat(result.get(2).getSequence()).isEqualTo(3);
    }

    @Test
    void should_check_exists_by_sequence() {
        boolean exists = quizAnswerOptionRepository
                .existsByQuizQuestionIdAndSequenceAndDeletedFalse(question.getId(), 1);

        assertThat(exists).isTrue();
    }

    @Test
    void should_not_consider_deleted_when_checking_sequence() {
        QuizAnswerOption deleted = createDeletedOption(
                "Option removed",
                question,
                5
        );

        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        boolean exists = quizAnswerOptionRepository
                .existsByQuizQuestionIdAndSequenceAndDeletedFalse(question.getId(), 5);

        assertThat(exists).isFalse();
    }

    @Test
    void should_ignore_current_id_when_checking_sequence_on_update() {
        boolean exists = quizAnswerOptionRepository
                .existsByQuizQuestionIdAndIdNotAndSequenceAndDeletedFalse(question.getId(), option1.getId(), 1);

        assertThat(exists).isFalse();
    }

    @Test
    void should_detect_duplicate_sequence_when_updating_other_option() {
        boolean exists = quizAnswerOptionRepository
                .existsByQuizQuestionIdAndIdNotAndSequenceAndDeletedFalse(question.getId(), option2.getId(), 1);

        assertThat(exists).isTrue();
    }

    @Test
    void should_check_exists_correct_option() {
        boolean exists = quizAnswerOptionRepository
                .existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(question.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void should_not_consider_deleted_correct_option() {
        QuizAnswerOption deleted = createDeletedOption(
                "Correct option removed",
                question,
                6
        );

        deleted.setCreatedAt(now);

        deleted.changeCorrect(true);

        entityManager.persist(deleted);

        boolean exists = quizAnswerOptionRepository
                .existsByQuizQuestionIdAndCorrectTrueAndDeletedFalse(question.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void should_ignore_current_id_when_checking_correct_on_update() {
        boolean exists = quizAnswerOptionRepository
                .existsByQuizQuestionIdAndIdNotAndCorrectTrueAndDeletedFalse(
                        question.getId(), correctOption.getId()
                );

        assertThat(exists).isFalse();
    }

    @Test
    void should_detect_other_correct_option_when_updating() {
        boolean exists = quizAnswerOptionRepository
                .existsByQuizQuestionIdAndIdNotAndCorrectTrueAndDeletedFalse(question.getId(), option1.getId());

        assertThat(exists).isTrue();
    }
}