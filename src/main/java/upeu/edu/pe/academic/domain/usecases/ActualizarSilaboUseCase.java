package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.commands.ActualizarSilaboCommand;
import upeu.edu.pe.academic.domain.entities.Silabo;
import upeu.edu.pe.academic.domain.entities.SilaboHistorial;
import upeu.edu.pe.academic.domain.repositories.SilaboRepository;

/**
 * Caso de uso: Actualizar un sílabo existente
 * 
 * Responsabilidades:
 * - Validar que el sílabo exista
 * - Validar que el sílabo sea modificable (no esté VIGENTE)
 * - Actualizar los campos
 * - Registrar en historial
 */
@ApplicationScoped
public class ActualizarSilaboUseCase {
    
    @Inject
    SilaboRepository silaboRepository;
    
    @Transactional
    public Silabo execute(ActualizarSilaboCommand command, String usuarioModificador) {
        // Buscar el sílabo
        Silabo silabo = silaboRepository.findByIdOptional(command.id())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Sílabo no encontrado con ID: " + command.id()
                ));
        
        // Validar que sea modificable
        if (!silabo.esModificable()) {
            throw new IllegalStateException(
                "No se puede modificar un sílabo en estado " + silabo.getEstado()
            );
        }
        
        // Actualizar campos
        if (command.competencias() != null) {
            silabo.setCompetencias(command.competencias());
        }
        if (command.sumilla() != null) {
            silabo.setSumilla(command.sumilla());
        }
        if (command.bibliografia() != null) {
            silabo.setBibliografia(command.bibliografia());
        }
        if (command.metodologia() != null) {
            silabo.setMetodologia(command.metodologia());
        }
        if (command.recursosDidacticos() != null) {
            silabo.setRecursosDidacticos(command.recursosDidacticos());
        }
        if (command.observaciones() != null) {
            silabo.setObservaciones(command.observaciones());
        }
        
        // Registrar en historial
        SilaboHistorial historial = new SilaboHistorial(
            silabo,
            "ACTUALIZACION",
            usuarioModificador != null ? usuarioModificador : "SYSTEM",
            "Sílabo actualizado"
        );
        silabo.getHistorial().add(historial);
        
        return silabo;
    }
}
