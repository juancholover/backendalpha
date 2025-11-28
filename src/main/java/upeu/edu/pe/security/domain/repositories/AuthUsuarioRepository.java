package upeu.edu.pe.security.domain.repositories;

import upeu.edu.pe.security.domain.entities.AuthUsuario;
import java.util.Optional;

public interface AuthUsuarioRepository {
    void persist(AuthUsuario authUsuario);

    Optional<AuthUsuario> findByIdOptional(Long id);

    Optional<AuthUsuario> findByUsername(String username);

    Optional<AuthUsuario> findByPersonaId(Long personaId);

    boolean existsByUsername(String username);

    boolean existsByPersonaId(Long personaId);

    long countByRol(Long rolId);

    java.util.List<AuthUsuario> findAllUsuarios();

    void delete(AuthUsuario authUsuario);
}
