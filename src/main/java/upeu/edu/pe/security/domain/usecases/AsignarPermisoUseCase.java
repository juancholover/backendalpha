package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.Permiso;
import upeu.edu.pe.security.domain.entities.Rol;
import upeu.edu.pe.security.domain.entities.RolPermiso;
import upeu.edu.pe.security.domain.repositories.PermisoRepository;
import upeu.edu.pe.security.domain.repositories.RolPermisoRepository;
import upeu.edu.pe.security.domain.repositories.RolRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class AsignarPermisoUseCase {

    private final RolPermisoRepository rolPermisoRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    @Inject
    public AsignarPermisoUseCase(RolPermisoRepository rolPermisoRepository,
            RolRepository rolRepository,
            PermisoRepository permisoRepository) {
        this.rolPermisoRepository = rolPermisoRepository;
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    public RolPermiso execute(Long rolId, Long permisoId, Boolean puedeDelegar, String restriccion) {
        Rol rol = rolRepository.findByIdOptional(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", rolId));

        Permiso permiso = permisoRepository.findByIdOptional(permisoId)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso", "id", permisoId));

        if (rolPermisoRepository.existsByRolAndPermiso(rolId, permisoId)) {
            throw new BusinessException("El permiso ya está asignado a este rol");
        }

        RolPermiso rolPermiso = RolPermiso.crear(rol, permiso, puedeDelegar, restriccion);
        rolPermisoRepository.persist(rolPermiso);
        return rolPermiso;
    }

    public void assignMultiple(Long rolId, List<Long> permisoIds) {
        Rol rol = rolRepository.findByIdOptional(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", rolId));

        for (Long permisoId : permisoIds) {
            Permiso permiso = permisoRepository.findByIdOptional(permisoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permiso", "id", permisoId));

            if (!rolPermisoRepository.existsByRolAndPermiso(rolId, permisoId)) {
                RolPermiso rolPermiso = RolPermiso.crear(rol, permiso, false, null);
                rolPermisoRepository.persist(rolPermiso);
            }
        }
    }
}
