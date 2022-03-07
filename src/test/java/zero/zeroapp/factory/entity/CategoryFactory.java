package zero.zeroapp.factory.entity;

import zero.zeroapp.entity.category.Category;

public class CategoryFactory {

    public static Category createCategory() {
        return new Category("name", null);
    }

    public static Category createCategory(String name, Category parent) {
        return new Category(name, parent);
    }

    public static Category createCategoryWithName(String name) {
        return new Category(name, null);
    }
}
