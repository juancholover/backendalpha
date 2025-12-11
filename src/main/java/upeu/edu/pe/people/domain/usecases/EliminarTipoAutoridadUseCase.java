package upeu.edu.pe.people.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.people.domain.entities.TipoAutoridad;
import upeu.edu.pe.people.domain.repositories.AutoridadRepository;
import upeu.edu.pe.people.domain.repositories.TipoAutoridadRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class EliminarTipoAutoridadUseCase {

    @Inject
    TipoAutoridadRepository tipoAutoridadRepository;

    @Inject
    AutoridadRepository autoridadRepository;

    @Transactional
    public void execute(Long id) {
        TipoAutoridad tipo = tipoAutoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de autoridad no encontrado con ID: " + id));

        // Validar que no tenga autoridades asociadas activas
        if (autoridadRepository.existsActivaByTipoAutoridadId(id)) {
            throw new BusinessException("No se puede eliminar: tiene autoridades activas asociadas");
        }

        tipo.setActive(false);
        tipoAutoridadRepository.persist(tipo);
    }
}
