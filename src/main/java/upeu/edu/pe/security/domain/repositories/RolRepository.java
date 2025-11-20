package upeu.edu.pe.security.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.Rol;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RolRepository implements PanacheRepository<Rol> {

    /**
     * Busca roles por universidad
     */
    public List<Rol> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true", universidadId).list();
    }

    /**
     * Busca un rol por nombre y universidad
     */
    public Optional<Rol> findByNombreAndUniversidad(String nombre, Long universidadId) {
        return find("UPPER(nombre) = UPPER(?1) and universidad.id = ?2 and active = true", 
                    nombre, universidadId).firstResultOptional();
    }

    /**
     * Busca roles del sistema
     */
    public List<Rol> findRolesSistema() {
        return find("esSistema = true and active = true").list();
    }

    /**
     * Busca roles activos por universidad
     */
    public List<Rol> findActiveByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true", universidadId).list();
    }

    /**
     * Verifica si existe un rol con ese nombre en la universidad
     */
    public boolean existsByNombreAndUniversidad(String nombre, Long universidadId) {
        return count("UPPER(nombre) = UPPER(?1) and universidad.id = ?2", nombre, universidadId) > 0;
    }

    /**
     * Cuenta usuarios con este rol
     */
    public long countUsuariosConRol(Long rolId) {
        // Consulta usando el repositorio de AuthUsuario
        return getEntityManager()
            .createQuery("SELECT COUNT(au) FROM AuthUsuario au WHERE au.rol.id = :rolId", Long.class)
            .setParameter("rolId", rolId)
            .getSingleResult();
    }

    /**
     * Busca roles con permisos específicos
     */
    public List<Rol> findByPermisoNombre(String permisoNombre, Long universidadId) {
        return find("SELECT DISTINCT r FROM Rol r " +
                   "JOIN r.rolPermisos rp " +
                   "JOIN rp.permiso p " +
                   "WHERE UPPER(p.nombreClave) = UPPER(?1) " +
                   "AND r.universidad.id = ?2 " +
                   "AND r.active = true", permisoNombre, universidadId).list();
    }
}
