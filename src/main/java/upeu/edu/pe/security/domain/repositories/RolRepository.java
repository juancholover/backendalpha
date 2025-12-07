package upeu.edu.pe.security.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.Rol;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RolRepository implements PanacheRepository<Rol> {

    /**
     * Busca roles activos
     */
    public List<Rol> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Busca un rol por nombre
     */
    public Optional<Rol> findByNombre(String nombre) {
        return find("UPPER(nombre) = UPPER(?1) and active = true", 
                    nombre).firstResultOptional();
    }

    /**
     * Busca roles del sistema
     */
    public List<Rol> findRolesSistema() {
        return find("esSistema = true and active = true").list();
    }

    /**
     * Busca roles activos
     */
    public List<Rol> findActive() {
        return find("active = true").list();
    }

    /**
     * Verifica si existe un rol con ese nombre
     */
    public boolean existsByNombre(String nombre) {
        return count("UPPER(nombre) = UPPER(?1) and active = true", nombre) > 0;
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
    public List<Rol> findByPermisoNombre(String permisoNombre) {
        return find("SELECT DISTINCT r FROM Rol r " +
                   "JOIN r.rolPermisos rp " +
                   "JOIN rp.permiso p " +
                   "WHERE UPPER(p.nombreClave) = UPPER(?1) " +
                   "AND r.active = true", permisoNombre).list();
    }
}
