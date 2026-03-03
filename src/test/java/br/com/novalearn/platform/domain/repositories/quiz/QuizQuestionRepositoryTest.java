package br.com.novalearn.platform.domain.repositories.quiz;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;
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
public class QuizQuestionRepositoryTest {
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;

    private User user;
    private Category category;
    private Course course;
    private Module module;
    private Quiz quiz;

    private QuizQuestion question1;
    private QuizQuestion question2;
    private QuizQuestion inactiveQuestion;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        user = createUser("a@test.com", "11111111111");

        category = createCategory("Backend");
        category.setCreatedAt(now);

        course = createCourse("Java", category, user);
        course.setCreatedAt(now);

        module = createModule("Fundamentos", course, 1);
        module.setCreatedAt(now);

        quiz = createQuiz("Quiz Java", module);
        quiz.setCreatedAt(now);

        question1 = createQuestion("O que é JVM?", quiz, 1, true);
        question1.setCreatedAt(now);

        question2 = createQuestion("O que é JDK?", quiz, 2, true);
        question2.setCreatedAt(now);

        inactiveQuestion = createQuestion("Questão inativa", quiz, 3, false);
        inactiveQuestion.setCreatedAt(now);

        persistAll();
    }

    private void persistAll() {
        entityManager.persist(user);
        entityManager.persist(category);
        entityManager.persist(course);
        entityManager.persist(module);
        entityManager.persist(quiz);

        entityManager.persist(question1);
        entityManager.persist(question2);
        entityManager.persist(inactiveQuestion);
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
            int sequence,
            boolean active
    ) {
        QuizQuestion question = QuizQuestion.create(
                quiz,
                sequence,
                title,
                5
        );

        question.setCreatedAt(now);

        if(!active) question.deactivate();

        return question;
    }

    private QuizQuestion createDeletedQuestion(
            String title,
            Quiz quiz,
            int sequence
    ) {
        QuizQuestion question = QuizQuestion.create(
                quiz,
                sequence,
                title,
                5
        );

        question.setCreatedAt(now);
        question.delete();

        return question;
    }

    @Test
    void should_find_all_not_deleted() {
        QuizQuestion deleted = createDeletedQuestion("Question removed", quiz, 4);
        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        List<QuizQuestion> result = quizQuestionRepository.findAllByDeletedFalse();

        assertThat(result).hasSize(3);
    }

    @Test
    void should_find_all_by_quiz_ordered_by_sequence() {
        List<QuizQuestion> result = quizQuestionRepository
                .findAllByQuizIdAndDeletedFalseOrderBySequenceAsc(quiz.getId());

        assertThat(result).hasSize(3);

        assertThat(result.get(0).getSequence()).isEqualTo(1);
        assertThat(result.get(1).getSequence()).isEqualTo(2);
        assertThat(result.get(2).getSequence()).isEqualTo(3);
    }

    @Test
    void should_find_only_active_questions_ordered_by_sequence() {
        List<QuizQuestion> result = quizQuestionRepository
                .findAllByQuizIdAndDeletedFalseAndActiveTrueOrderBySequenceAsc(quiz.getId());

        assertThat(result).hasSize(2);

        assertThat(result).allMatch(QuizQuestion::isActive);
    }

    @Test
    void should_check_exists_by_sequence() {
        boolean exists = quizQuestionRepository
                .existsByQuizIdAndSequenceAndDeletedFalse(quiz.getId(), 1);

        assertThat(exists).isTrue();
    }

    @Test
    void should_not_consider_deleted_when_checking_sequence() {
        QuizQuestion deleted = createDeletedQuestion(
                "Question removed",
                quiz,
                5
        );

        deleted.setCreatedAt(now);

        entityManager.persist(deleted);

        boolean exists = quizQuestionRepository
                .existsByQuizIdAndSequenceAndDeletedFalse(quiz.getId(), 5);

        assertThat(exists).isFalse();
    }

    @Test
    void should_ignore_current_id_when_checking_sequence_on_update() {
        boolean exists = quizQuestionRepository
                .existsByQuizIdAndIdNotAndSequenceAndDeletedFalse(quiz.getId(), question1.getId(), 1);

        assertThat(exists).isFalse();
    }

    @Test
    void should_detect_duplicate_sequence_when_updating_other_question() {
        boolean exists = quizQuestionRepository
                .existsByQuizIdAndIdNotAndSequenceAndDeletedFalse(quiz.getId(), question2.getId(), 1);

        assertThat(exists).isTrue();
    }

    @Test
    void should_count_questions_by_quiz() {
        long count = quizQuestionRepository.countByQuizIdAndDeletedFalse(quiz.getId());
        assertThat(count).isEqualTo(3);
    }
}