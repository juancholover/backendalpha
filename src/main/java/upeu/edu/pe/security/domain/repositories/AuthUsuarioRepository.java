package upeu.edu.pe.security.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.AuthUsuario;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AuthUsuarioRepository implements PanacheRepositoryBase<AuthUsuario, Long> {

    /**
     * Buscar usuario por username (email de persona)
     */
    public Optional<AuthUsuario> findByUsername(String username) {
        return find("persona.email = ?1 and active = true", username).firstResultOptional();
    }

    /**
     * Buscar usuario por email (delegado a persona)
     */
    public Optional<AuthUsuario> findByEmail(String email) {
        return find("persona.email = ?1 and active = true", email).firstResultOptional();
    }

    /**
     * Buscar usuario por persona
     */
    public Optional<AuthUsuario> findByPersona(Long personaId) {
        return find("persona.id = ?1 and active = true", personaId).firstResultOptional();
    }

    /**
     * Listar usuarios por universidad
     */
    public List<AuthUsuario> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true", universidadId).list();
    }

    /**
     * Listar usuarios activos (no bloqueados)
     */
    public List<AuthUsuario> findUsuariosActivos() {
        return find("active = true and fechaBloqueo is null").list();
    }

    /**
     * Listar usuarios bloqueados
     */
    public List<AuthUsuario> findUsuariosBloqueados() {
        return find("active = true and fechaBloqueo is not null").list();
    }

    /**
     * Buscar usuario por token de recuperación
     */
    public Optional<AuthUsuario> findByTokenRecuperacion(String tokenRecuperacion) {
        return find("tokenRecuperacion = ?1 and active = true", tokenRecuperacion).firstResultOptional();
    }

    /**
     * Listar todos los usuarios activos
     */
    public List<AuthUsuario> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si existe username (email de persona)
     */
    public boolean existsByUsername(String username) {
        return count("persona.email = ?1 and active = true", username) > 0;
    }

    /**
     * Verificar si existe username excluyendo un ID
     */
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return count("persona.email = ?1 and id != ?2 and active = true", username, id) > 0;
    }

    /**
     * Verificar si existe email (delegado a persona)
     */
    public boolean existsByEmail(String email) {
        return count("persona.email = ?1 and active = true", email) > 0;
    }

    /**
     * Verificar si existe email excluyendo un ID
     */
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return count("persona.email = ?1 and id != ?2 and active = true", email, id) > 0;
    }

    /**
     * Verificar si una persona ya tiene usuario
     */
    public boolean existsByPersona(Long personaId) {
        return count("persona.id = ?1 and active = true", personaId) > 0;
    }
}
