package br.com.novalearn.platform.factories.entities.lesson;

import br.com.novalearn.platform.domain.entities.lesson.Lesson;
import br.com.novalearn.platform.domain.entities.module.Module;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;
import static br.com.novalearn.platform.factories.entities.module.CreateModuleFactory.createInitializedModule;

public final class CreateLessonFactory {
    private static final Module module = createInitializedModule(createInitializedCourse());

    public static Lesson createInitializedLesson() {
        return Lesson.create(
                module,
                "First lesson",
                "Some description",
                1,
                300,
                true,
                true,
                null,
                null,
                "Observations"
        );
    }
}