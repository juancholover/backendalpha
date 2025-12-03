package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.exceptions.LimiteEstudiantesExcedidoException;
import upeu.edu.pe.academic.domain.exceptions.SubscripcionVencidaException;
import upeu.edu.pe.academic.domain.exceptions.UniversidadNoEncontradaException;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;

/**
 * Caso de uso: Validar límites y estado de una universidad.
 * 
 * Usado antes de operaciones críticas como crear estudiantes o docentes.
 */
@ApplicationScoped
public class ValidarLimitesUniversidadUseCase {
    
    @Inject
    UniversidadRepository repository;
    
   
    public void validarAgregarEstudiante(Long universidadId) {
        Universidad universidad = repository.findByIdOptional(universidadId)
            .orElseThrow(() -> new UniversidadNoEncontradaException(universidadId));
        
        // Validar estado activo y suscripción vigente
        if (!universidad.estaActiva()) {
            throw new SubscripcionVencidaException(universidadId);
        }
        
        // Validar límite de estudiantes
        if (universidad.haExcedidoLimiteEstudiantes()) {
            throw new LimiteEstudiantesExcedidoException(
                universidad.getMaxEstudiantes(),
                universidad.getTotalEstudiantes()
            );
        }
    }
    
    /**
     * Incrementa el contador de estudiantes.
     * 
     * Debe llamarse después de crear un estudiante exitosamente.
     */
    public void incrementarEstudiantes(Long universidadId) {
        Universidad universidad = repository.findByIdOptional(universidadId)
            .orElseThrow(() -> new UniversidadNoEncontradaException(universidadId));
        
        universidad.setTotalEstudiantes(universidad.getTotalEstudiantes() + 1);
    }
    
    /**
     * Decrementa el contador de estudiantes.
     * 
     * Debe llamarse después de eliminar un estudiante.
     */
    public void decrementarEstudiantes(Long universidadId) {
        Universidad universidad = repository.findByIdOptional(universidadId)
            .orElseThrow(() -> new UniversidadNoEncontradaException(universidadId));
        
        if (universidad.getTotalEstudiantes() > 0) {
            universidad.setTotalEstudiantes(universidad.getTotalEstudiantes() - 1);
        }
    }
}
