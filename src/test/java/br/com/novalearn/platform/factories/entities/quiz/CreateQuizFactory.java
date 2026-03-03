package br.com.novalearn.platform.factories.entities.quiz;

import br.com.novalearn.platform.domain.entities.module.Module;
import br.com.novalearn.platform.domain.entities.quiz.Quiz;

import java.math.BigDecimal;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;

public final class CreateQuizFactory {
    private static final Module module = createInitializedModule(createInitializedCourse());

    public static Quiz createInitializedQuiz() {
        Quiz quiz = Quiz.create(
                module,
                "Quiz",
                5,
                BigDecimal.TEN,
                3,
                false
        );

        quiz.activate();
        quiz.markAsNotDeleted();

        return quiz;
    }
}