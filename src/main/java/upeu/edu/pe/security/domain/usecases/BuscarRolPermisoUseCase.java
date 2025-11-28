package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.RolPermiso;
import upeu.edu.pe.security.domain.repositories.RolPermisoRepository;

import java.util.List;

@ApplicationScoped
public class BuscarRolPermisoUseCase {

    private final RolPermisoRepository rolPermisoRepository;

    @Inject
    public BuscarRolPermisoUseCase(RolPermisoRepository rolPermisoRepository) {
        this.rolPermisoRepository = rolPermisoRepository;
    }

    public List<RolPermiso> findByRol(Long rolId) {
        return rolPermisoRepository.findByRol(rolId);
    }

    public List<RolPermiso> findByPermiso(Long permisoId) {
        return rolPermisoRepository.findByPermiso(permisoId);
    }

    public List<RolPermiso> findDelegablesByRol(Long rolId) {
        return rolPermisoRepository.findDelegablesByRol(rolId);
    }

    public List<RolPermiso> findByRolAndModulo(Long rolId, String modulo) {
        return rolPermisoRepository.findByRolAndModulo(rolId, modulo);
    }

    public long countByRol(Long rolId) {
        return rolPermisoRepository.countByRol(rolId);
    }

    public boolean existsByRolAndPermiso(Long rolId, Long permisoId) {
        return rolPermisoRepository.existsByRolAndPermiso(rolId, permisoId);
    }
}
