package upeu.edu.pe.security.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.User;
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public List<User> findAllActive() {
        return list("active = true ORDER BY username");
    }

    public List<User> findAllByStatus(UserStatus status) {
        return list("status = ?1 and active = true ORDER BY username", status);
    }

    public List<User> findAllByRole(UserRole role) {
        return list("role = ?1 and active = true ORDER BY username", role);
    }

    public Optional<User> findByUsername(String username) {
        return find("UPPER(username) = UPPER(?1) and active = true", username).firstResultOptional();
    }

    public Optional<User> findByEmail(String email) {
        return find("UPPER(email) = UPPER(?1) and active = true", email).firstResultOptional();
    }

    public boolean existsByUsername(String username) {
        return count("UPPER(username) = UPPER(?1) and active = true", username) > 0;
    }

    public boolean existsByEmail(String email) {
        return count("UPPER(email) = UPPER(?1) and active = true", email) > 0;
    }

    public long countByRole(UserRole role) {
        return count("role = ?1 and active = true", role);
    }

    public long countByStatus(UserStatus status) {
        return count("status = ?1 and active = true", status);
    }
}