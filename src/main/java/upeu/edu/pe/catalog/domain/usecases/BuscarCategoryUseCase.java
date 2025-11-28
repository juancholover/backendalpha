package upeu.edu.pe.catalog.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.catalog.domain.entities.Category;
import upeu.edu.pe.catalog.domain.repositories.CategoryRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarCategoryUseCase {

    private final CategoryRepository categoryRepository;

    @Inject
    public BuscarCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category findById(Long id) {
        return categoryRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
    }

    public List<Category> findAll() {
        return categoryRepository.listAll();
    }

    public List<Category> search(String query) {
        return categoryRepository.search(query);
    }
}
