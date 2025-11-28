package upeu.edu.pe.catalog.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.catalog.domain.entities.Category;
import upeu.edu.pe.catalog.domain.repositories.CategoryRepository;

@ApplicationScoped
public class CrearCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public CrearCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category execute(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category with name " + name + " already exists");
        }
        Category category = Category.crear(name, description);
        return categoryRepository.save(category);
    }
}
