package upeu.edu.pe.catalog.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.catalog.domain.entities.Category;
import upeu.edu.pe.catalog.domain.repositories.CategoryRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class ActualizarCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public ActualizarCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category execute(Long id, String name, String description) {
        Category category = categoryRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));

        if (!category.getName().equalsIgnoreCase(name) && categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category with name " + name + " already exists");
        }

        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }
}
