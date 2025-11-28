package upeu.edu.pe.catalog.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.catalog.domain.repositories.CategoryRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public EliminarCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void execute(Long id) {
        if (categoryRepository.findByIdOptional(id).isEmpty()) {
            throw new ResourceNotFoundException("Category not found with id " + id);
        }
        categoryRepository.delete(id);
    }
}
