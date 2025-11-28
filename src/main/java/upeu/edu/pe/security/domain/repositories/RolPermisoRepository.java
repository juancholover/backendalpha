package upeu.edu.pe.security.domain.repositories;

import upeu.edu.pe.security.domain.entities.RolPermiso;
import java.util.List;
import java.util.Optional;

public interface RolPermisoRepository {
    void persist(RolPermiso rolPermiso);

    Optional<RolPermiso> findByIdOptional(Long id);

    List<RolPermiso> findByRol(Long rolId);

    List<RolPermiso> findByPermiso(Long permisoId);

    boolean existsByRolAndPermiso(Long rolId, Long permisoId);

    Optional<RolPermiso> findByRolAndPermiso(Long rolId, Long permisoId);

    List<RolPermiso> findDelegablesByRol(Long rolId);

    long deleteByRol(Long rolId);

    long countByRol(Long rolId);

    List<RolPermiso> findByRolAndModulo(Long rolId, String modulo);

    void delete(RolPermiso rolPermiso);
}
