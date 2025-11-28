package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.RolPermiso;
import upeu.edu.pe.security.domain.repositories.RolPermisoRepository;
import upeu.edu.pe.security.domain.repositories.RolRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class RemoverPermisoUseCase {

    private final RolPermisoRepository rolPermisoRepository;
    private final RolRepository rolRepository;

    @Inject
    public RemoverPermisoUseCase(RolPermisoRepository rolPermisoRepository, RolRepository rolRepository) {
        this.rolPermisoRepository = rolPermisoRepository;
        this.rolRepository = rolRepository;
    }

    public void execute(Long rolId, Long permisoId) {
        RolPermiso rolPermiso = rolPermisoRepository.findByRolAndPermiso(rolId, permisoId)
                .orElseThrow(() -> new NotFoundException("No se encontró la asignación del permiso al rol"));

        rolPermisoRepository.delete(rolPermiso);
    }

    public void removeAllByRol(Long rolId) {
        // Validar que el rol existe
        rolRepository.findByIdOptional(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", rolId));

        long count = rolPermisoRepository.deleteByRol(rolId);

        if (count == 0) {
            throw new NotFoundException("No se encontraron permisos asignados al rol");
        }
    }

    public void removeMultiple(Long rolId, List<Long> permisoIds) {
        // Validar que el rol existe
        rolRepository.findByIdOptional(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", rolId));

        for (Long permisoId : permisoIds) {
            rolPermisoRepository.findByRolAndPermiso(rolId, permisoId)
                    .ifPresent(rolPermisoRepository::delete);
        }
    }
}
