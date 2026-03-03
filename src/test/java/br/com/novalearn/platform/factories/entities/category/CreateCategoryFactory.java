package br.com.novalearn.platform.factories.entities.category;

import br.com.novalearn.platform.domain.entities.category.Category;

public final class CreateCategoryFactory {
    public static Category createInitializedCategory() {
        return Category.create(
                "Backend",
                "BCK",
                "Some Description",
                "Observations"
        );
    }
}