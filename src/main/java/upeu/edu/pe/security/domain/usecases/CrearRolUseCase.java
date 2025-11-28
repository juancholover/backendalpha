package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.security.domain.entities.Rol;
import upeu.edu.pe.security.domain.repositories.RolRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class CrearRolUseCase {

    private final RolRepository rolRepository;
    private final UniversidadRepository universidadRepository;

    @Inject
    public CrearRolUseCase(RolRepository rolRepository, UniversidadRepository universidadRepository) {
        this.rolRepository = rolRepository;
        this.universidadRepository = universidadRepository;
    }

    public Rol execute(Long universidadId, String nombre, String descripcion, Boolean esSistema) {
        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));

        if (rolRepository.existsByNombre(nombre, universidadId)) {
            throw new BusinessException("Ya existe un rol con el nombre " + nombre + " en esta universidad");
        }

        Rol rol = Rol.crear(universidad, nombre, descripcion, esSistema);
        rolRepository.persist(rol);
        return rol;
    }
}
