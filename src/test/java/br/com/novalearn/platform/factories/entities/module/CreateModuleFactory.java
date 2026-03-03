package br.com.novalearn.platform.factories.entities.module;

import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.module.Module;

import static br.com.novalearn.platform.factories.entities.course.CreateCourseFactory.createInitializedCourse;

public final class CreateModuleFactory {
    public static Module createInitializedModule(Course course) {
        return Module.create(
                course,
                "Module",
                "First Module",
                1,
                "Module in development"
        );
    }
}