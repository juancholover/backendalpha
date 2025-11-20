package upeu.edu.pe.security.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.RolPermiso;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RolPermisoRepository implements PanacheRepository<RolPermiso> {

    /**
     * Busca permisos de un rol específico
     */
    public List<RolPermiso> findByRol(Long rolId) {
        return find("rol.id = ?1 and active = true", rolId).list();
    }

    /**
     * Busca roles que tienen un permiso específico
     */
    public List<RolPermiso> findByPermiso(Long permisoId) {
        return find("permiso.id = ?1 and active = true", permisoId).list();
    }

    /**
     * Verifica si un rol tiene un permiso específico
     */
    public boolean existsByRolAndPermiso(Long rolId, Long permisoId) {
        return count("rol.id = ?1 and permiso.id = ?2", rolId, permisoId) > 0;
    }

    /**
     * Busca una relación específica rol-permiso
     */
    public Optional<RolPermiso> findByRolAndPermiso(Long rolId, Long permisoId) {
        return find("rol.id = ?1 and permiso.id = ?2", rolId, permisoId).firstResultOptional();
    }

    /**
     * Busca permisos delegables de un rol
     */
    public List<RolPermiso> findDelegablesByRol(Long rolId) {
        return find("rol.id = ?1 and puedeDeleagar = true and active = true", rolId).list();
    }

    /**
     * Elimina todos los permisos de un rol
     */
    public long deleteByRol(Long rolId) {
        return delete("rol.id = ?1", rolId);
    }

    /**
     * Cuenta permisos de un rol
     */
    public long countByRol(Long rolId) {
        return count("rol.id = ?1", rolId);
    }

    /**
     * Busca permisos de un rol por módulo
     */
    public List<RolPermiso> findByRolAndModulo(Long rolId, String modulo) {
        return find("rol.id = ?1 and UPPER(permiso.modulo) = UPPER(?2) and active = true", 
                   rolId, modulo).list();
    }
}
