package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.Rol;
import upeu.edu.pe.security.domain.repositories.RolRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarRolUseCase {

    private final RolRepository rolRepository;

    @Inject
    public EliminarRolUseCase(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public void execute(Long id) {
        Rol rol = rolRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));

        if (rol.getEsSistema()) {
            throw new BusinessException("No se puede eliminar un rol de sistema");
        }

        rolRepository.delete(rol);
    }
}
