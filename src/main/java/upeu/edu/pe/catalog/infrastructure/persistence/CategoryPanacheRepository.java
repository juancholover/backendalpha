package upeu.edu.pe.catalog.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.catalog.domain.entities.Category;
import upeu.edu.pe.catalog.domain.repositories.CategoryRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryPanacheRepository implements CategoryRepository, PanacheRepository<Category> {

    @Override
    public Category save(Category category) {
        persist(category);
        return category;
    }

    @Override
    public Optional<Category> findByIdOptional(Long id) {
        return find("id = ?1 and active = true", id).firstResultOptional();
    }

    @Override
    public List<Category> listAll() {
        return list("active = true ORDER BY name");
    }

    @Override
    public void delete(Long id) {
        findByIdOptional(id).ifPresent(category -> {
            category.setActive(false);
            persist(category);
        });
    }

    @Override
    public List<Category> search(String query) {
        return list("active = true and (UPPER(name) like UPPER(?1) or UPPER(description) like UPPER(?1))",
                "%" + query + "%");
    }

    @Override
    public Optional<Category> findByName(String name) {
        return find("UPPER(name) = UPPER(?1) and active = true", name).firstResultOptional();
    }

    @Override
    public boolean existsByName(String name) {
        return count("UPPER(name) = UPPER(?1) and active = true", name) > 0;
    }
}
