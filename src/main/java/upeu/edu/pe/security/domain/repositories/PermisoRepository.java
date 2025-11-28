package upeu.edu.pe.security.domain.repositories;

import upeu.edu.pe.security.domain.entities.Permiso;
import java.util.List;
import java.util.Optional;

public interface PermisoRepository {
    void persist(Permiso permiso);

    Optional<Permiso> findByIdOptional(Long id);

    Optional<Permiso> findByNombreClave(String nombreClave);

    List<Permiso> findByModulo(String modulo);

    List<Permiso> findByRecurso(String recurso);

    List<Permiso> findByAccion(String accion);

    List<Permiso> findByModuloAndRecurso(String modulo, String recurso);

    List<Permiso> findAllActive();

    boolean existsByNombreClave(String nombreClave);

    List<Permiso> findByRol(Long rolId);

    List<Permiso> search(String query);
}
