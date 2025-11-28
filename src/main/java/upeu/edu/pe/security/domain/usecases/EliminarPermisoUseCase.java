package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.Permiso;
import upeu.edu.pe.security.domain.repositories.PermisoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarPermisoUseCase {

    private final PermisoRepository permisoRepository;

    @Inject
    public EliminarPermisoUseCase(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    public void execute(Long id) {
        Permiso permiso = permisoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso", "id", id));

        if (permiso.getRolPermisos() != null && !permiso.getRolPermisos().isEmpty()) {
            throw new BusinessException("No se puede eliminar el permiso porque está asignado a " +
                    permiso.getRolPermisos().size() + " rol(es)");
        }

        permiso.setActive(false);
        permisoRepository.persist(permiso);
    }
}
