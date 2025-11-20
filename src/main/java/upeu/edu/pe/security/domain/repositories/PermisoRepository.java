package upeu.edu.pe.security.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.Permiso;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PermisoRepository implements PanacheRepository<Permiso> {

    /**
     * Busca un permiso por nombre clave
     */
    public Optional<Permiso> findByNombreClave(String nombreClave) {
        return find("UPPER(nombreClave) = UPPER(?1) and active = true", nombreClave).firstResultOptional();
    }

    /**
     * Busca permisos por módulo
     */
    public List<Permiso> findByModulo(String modulo) {
        return find("UPPER(modulo) = UPPER(?1) and active = true", modulo).list();
    }

    /**
     * Busca permisos por recurso
     */
    public List<Permiso> findByRecurso(String recurso) {
        return find("UPPER(recurso) = UPPER(?1) and active = true", recurso).list();
    }

    /**
     * Busca permisos por acción
     */
    public List<Permiso> findByAccion(String accion) {
        return find("UPPER(accion) = UPPER(?1) and active = true", accion).list();
    }

    /**
     * Busca permisos por módulo y recurso
     */
    public List<Permiso> findByModuloAndRecurso(String modulo, String recurso) {
        return find("UPPER(modulo) = UPPER(?1) and UPPER(recurso) = UPPER(?2) and active = true", 
                   modulo, recurso).list();
    }

    /**
     * Busca todos los permisos activos agrupados por módulo
     */
    public List<Permiso> findAllActive() {
        return find("active = true ORDER BY modulo, recurso, accion").list();
    }

    /**
     * Verifica si existe un permiso con ese nombre clave
     */
    public boolean existsByNombreClave(String nombreClave) {
        return count("UPPER(nombreClave) = UPPER(?1)", nombreClave) > 0;
    }

    /**
     * Busca permisos asignados a un rol específico
     */
    public List<Permiso> findByRol(Long rolId) {
        return find("SELECT p FROM Permiso p " +
                   "JOIN RolPermiso rp ON rp.permiso.id = p.id " +
                   "WHERE rp.rol.id = ?1 " +
                   "AND p.active = true", rolId).list();
    }

    /**
     * Búsqueda por texto en nombre clave o descripción
     */
    public List<Permiso> search(String query) {
        String searchPattern = "%" + query.toUpperCase() + "%";
        return find("(UPPER(nombreClave) LIKE ?1 OR UPPER(descripcion) LIKE ?1) and active = true " +
                   "ORDER BY modulo, recurso", searchPattern).list();
    }
}
