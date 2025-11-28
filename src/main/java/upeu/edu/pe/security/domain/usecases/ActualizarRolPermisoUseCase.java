package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.RolPermiso;
import upeu.edu.pe.security.domain.repositories.RolPermisoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class ActualizarRolPermisoUseCase {

    private final RolPermisoRepository rolPermisoRepository;

    @Inject
    public ActualizarRolPermisoUseCase(RolPermisoRepository rolPermisoRepository) {
        this.rolPermisoRepository = rolPermisoRepository;
    }

    public RolPermiso updateRestriccion(Long rolId, Long permisoId, String restriccion) {
        RolPermiso rolPermiso = rolPermisoRepository.findByRolAndPermiso(rolId, permisoId)
                .orElseThrow(() -> new NotFoundException("No se encontró la asignación del permiso al rol"));

        rolPermiso.setRestriccion(restriccion);
        rolPermisoRepository.persist(rolPermiso);
        return rolPermiso;
    }

    public RolPermiso toggleDelegacion(Long rolId, Long permisoId) {
        RolPermiso rolPermiso = rolPermisoRepository.findByRolAndPermiso(rolId, permisoId)
                .orElseThrow(() -> new NotFoundException("No se encontró la asignación del permiso al rol"));

        rolPermiso.setPuedeDelegar(!rolPermiso.getPuedeDelegar());
        rolPermisoRepository.persist(rolPermiso);
        return rolPermiso;
    }
}
