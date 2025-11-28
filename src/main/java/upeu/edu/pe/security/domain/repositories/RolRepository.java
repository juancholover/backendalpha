package upeu.edu.pe.security.domain.repositories;

import upeu.edu.pe.security.domain.entities.Rol;
import java.util.List;
import java.util.Optional;

public interface RolRepository {
    void persist(Rol rol);

    Optional<Rol> findByIdOptional(Long id);

    Optional<Rol> findByNombre(String nombre);

    List<Rol> findAllRoles();

    List<Rol> findByUniversidad(Long universidadId);

    List<Rol> findByPermisoNombre(String permisoNombre, Long universidadId);

    boolean existsByNombre(String nombre, Long universidadId);

    void delete(Rol rol);
}
