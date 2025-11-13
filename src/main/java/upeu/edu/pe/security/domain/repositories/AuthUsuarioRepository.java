package upeu.edu.pe.security.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.AuthUsuario;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AuthUsuarioRepository implements PanacheRepositoryBase<AuthUsuario, Long> {

    /**
     * Buscar usuario por username
     */
    public Optional<AuthUsuario> findByUsername(String username) {
        return find("username = ?1 and active = true", username).firstResultOptional();
    }

    /**
     * Buscar usuario por email
     */
    public Optional<AuthUsuario> findByEmail(String email) {
        return find("email = ?1 and active = true", email).firstResultOptional();
    }

    /**
     * Buscar usuario por persona
     */
    public Optional<AuthUsuario> findByPersona(Long personaId) {
        return find("persona.id = ?1 and active = true", personaId).firstResultOptional();
    }

    /**
     * Listar usuarios por rol
     */
    public List<AuthUsuario> findByRol(String rol) {
        return find("rol = ?1 and active = true", rol).list();
    }

    /**
     * Listar usuarios por estado
     */
    public List<AuthUsuario> findByEstado(String estado) {
        return find("estado = ?1 and active = true", estado).list();
    }

    /**
     * Listar usuarios activos
     */
    public List<AuthUsuario> findUsuariosActivos() {
        return find("estado = 'ACTIVO' and active = true").list();
    }

    /**
     * Listar usuarios bloqueados
     */
    public List<AuthUsuario> findUsuariosBloqueados() {
        return find("estado = 'BLOQUEADO' and active = true").list();
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
     * Verificar si existe username
     */
    public boolean existsByUsername(String username) {
        return count("username = ?1 and active = true", username) > 0;
    }

    /**
     * Verificar si existe username excluyendo un ID
     */
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return count("username = ?1 and id != ?2 and active = true", username, id) > 0;
    }

    /**
     * Verificar si existe email
     */
    public boolean existsByEmail(String email) {
        return count("email = ?1 and active = true", email) > 0;
    }

    /**
     * Verificar si existe email excluyendo un ID
     */
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return count("email = ?1 and id != ?2 and active = true", email, id) > 0;
    }

    /**
     * Verificar si una persona ya tiene usuario
     */
    public boolean existsByPersona(Long personaId) {
        return count("persona.id = ?1 and active = true", personaId) > 0;
    }
}
