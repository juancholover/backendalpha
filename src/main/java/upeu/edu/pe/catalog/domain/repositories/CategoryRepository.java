package upeu.edu.pe.catalog.domain.repositories;

import upeu.edu.pe.catalog.domain.entities.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);

    Optional<Category> findByIdOptional(Long id);

    List<Category> listAll();

    void delete(Long id);

    List<Category> search(String query);

    Optional<Category> findByName(String name);

    boolean existsByName(String name);
}