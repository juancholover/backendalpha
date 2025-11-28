package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.Permiso;
import upeu.edu.pe.security.domain.repositories.PermisoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class ActualizarPermisoUseCase {

    private final PermisoRepository permisoRepository;

    @Inject
    public ActualizarPermisoUseCase(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    public Permiso execute(Long id, String nombreClave, String descripcion, String modulo, String recurso,
            String accion) {
        Permiso permiso = permisoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso", "id", id));

        if (nombreClave != null && !nombreClave.isEmpty()) {
            permiso.setNombreClave(nombreClave);
            permiso.setDescripcion(descripcion);
            permiso.setModulo(modulo);
            permiso.setRecurso(recurso);
            permiso.setAccion(accion);
        } else {
            permiso.actualizar(descripcion, modulo, recurso, accion);
        }

        permisoRepository.findByNombreClave(permiso.getNombreClave())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BusinessException("Ya existe un permiso con esa combinación");
                    }
                });

        permisoRepository.persist(permiso);
        return permiso;
    }
}
