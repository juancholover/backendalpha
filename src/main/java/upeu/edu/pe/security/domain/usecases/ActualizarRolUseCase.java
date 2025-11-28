package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.Rol;
import upeu.edu.pe.security.domain.repositories.RolRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class ActualizarRolUseCase {

    private final RolRepository rolRepository;

    @Inject
    public ActualizarRolUseCase(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public Rol execute(Long id, String nombre, String descripcion) {
        Rol rol = rolRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));

        if (rol.getEsSistema()) {
            throw new BusinessException("No se puede modificar un rol de sistema");
        }

        if (!rol.getNombre().equalsIgnoreCase(nombre)) {
            if (rolRepository.existsByNombre(nombre, rol.getUniversidad().getId())) {
                throw new BusinessException("Ya existe un rol con el nombre " + nombre + " en esta universidad");
            }
        }

        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);

        rolRepository.persist(rol);
        return rol;
    }
}
