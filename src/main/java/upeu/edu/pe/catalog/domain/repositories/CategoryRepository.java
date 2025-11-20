package upeu.edu.pe.catalog.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.catalog.domain.entities.Category;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {

    public List<Category> findAllActive() {
        return list("active = true ORDER BY name");
    }

    public Optional<Category> findByIdActive(Long id) {
        return find("id = ?1 and active = true", id).firstResultOptional();
    }

    public Optional<Category> findByName(String name) {
        return find("UPPER(name) = UPPER(?1) and active = true", name).firstResultOptional();
    }

    public boolean existsByName(String name) {
        return count("UPPER(name) = UPPER(?1) and active = true", name) > 0;
    }
}