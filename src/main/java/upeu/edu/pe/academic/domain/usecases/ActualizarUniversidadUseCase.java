package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.commands.ActualizarUniversidadCommand;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.exceptions.UniversidadNoEncontradaException;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;

/**
 * Caso de uso: Actualizar datos de una universidad existente.
 * 
 * Solo actualiza los campos proporcionados (no nulos).
 */
@ApplicationScoped
public class ActualizarUniversidadUseCase {
    
    @Inject
    UniversidadRepository repository;
    
   
    public Universidad execute(ActualizarUniversidadCommand command) {
        // 1. Buscar la universidad
        Universidad universidad = repository.findByIdOptional(command.id())
            .orElseThrow(() -> new UniversidadNoEncontradaException(command.id()));
        
        // 2. Actualizar solo campos proporcionados (no nulos)
        if (command.nombre() != null) {
            universidad.setNombre(command.nombre());
        }
        
        if (command.tipo() != null) {
            universidad.setTipo(command.tipo().toUpperCase());
        }
        
        if (command.plan() != null) {
            universidad.setPlan(command.plan().toUpperCase());
        }
        
        if (command.dominio() != null) {
            universidad.setDominio(command.dominio().toLowerCase());
        }
        
        if (command.website() != null) {
            universidad.setWebsite(command.website());
        }
        
        if (command.estado() != null) {
            universidad.setEstado(command.estado().toUpperCase());
        }
        
        if (command.maxEstudiantes() != null) {
            universidad.setMaxEstudiantes(command.maxEstudiantes());
        }
        
        if (command.maxDocentes() != null) {
            universidad.setMaxDocentes(command.maxDocentes());
        }
        
     
        return universidad;
    }
}
