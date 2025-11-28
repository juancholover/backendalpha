package upeu.edu.pe.security.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;

import java.util.Optional;

@ApplicationScoped
public class AuthUsuarioPanacheRepository implements AuthUsuarioRepository, PanacheRepository<AuthUsuario> {

    @Override
    public void persist(AuthUsuario authUsuario) {
        PanacheRepository.super.persist(authUsuario);
    }

    @Override
    public Optional<AuthUsuario> findByIdOptional(Long id) {
        return PanacheRepository.super.findByIdOptional(id);
    }

    @Override
    public Optional<AuthUsuario> findByUsername(String username) {
        // Asumiendo que el username es el email de la persona asociada
        return find("persona.email = ?1", username).firstResultOptional();
    }

    @Override
    public Optional<AuthUsuario> findByPersonaId(Long personaId) {
        return find("persona.id = ?1", personaId).firstResultOptional();
    }

    @Override
    public boolean existsByUsername(String username) {
        return count("persona.email = ?1", username) > 0;
    }

    @Override
    public boolean existsByPersonaId(Long personaId) {
        return count("persona.id = ?1", personaId) > 0;
    }

    @Override
    public long countByRol(Long rolId) {
        return count("rol.id = ?1", rolId);
    }

    @Override
    public java.util.List<AuthUsuario> findAllUsuarios() {
        return PanacheRepository.super.findAll().list();
    }

    @Override
    public void delete(AuthUsuario authUsuario) {
        PanacheRepository.super.delete(authUsuario);
    }
}
