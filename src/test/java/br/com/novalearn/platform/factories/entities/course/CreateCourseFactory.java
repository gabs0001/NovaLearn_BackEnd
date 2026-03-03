package br.com.novalearn.platform.factories.entities.course;

import br.com.novalearn.platform.domain.entities.category.Category;
import br.com.novalearn.platform.domain.entities.course.Course;
import br.com.novalearn.platform.domain.entities.user.User;

import static br.com.novalearn.platform.factories.entities.category.CreateCategoryFactory.createInitializedCategory;
import static br.com.novalearn.platform.factories.entities.user.CreateUserFactory.createInitializedUser;

public final class CreateCourseFactory {
    private static final String categoryName = "Java Fundamentals";
    private static final Category category = createInitializedCategory();
    private static final User user = createInitializedUser();

    public static Course createInitializedCourse() {
        return Course.create(categoryName, category, user);
    }
}