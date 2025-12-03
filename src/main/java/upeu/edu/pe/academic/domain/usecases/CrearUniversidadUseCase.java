package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.commands.CrearUniversidadCommand;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.exceptions.CodigoDuplicadoException;
import upeu.edu.pe.academic.domain.exceptions.RucDuplicadoException;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;

/**
 * Caso de uso: Crear una nueva universidad en el sistema.
 * 
 * Responsabilidades:
 * - Validar que el código no esté duplicado
 * - Validar que el RUC no esté duplicado
 * - Crear la entidad con valores por defecto
 * - Persistir la nueva universidad
 */
@ApplicationScoped
public class CrearUniversidadUseCase {
    
    @Inject
    UniversidadRepository repository;
  
    public Universidad execute(CrearUniversidadCommand command) {
    
        validarCodigoUnico(command.codigo());
        validarRucUnico(command.ruc());
        
       
        Universidad universidad = new Universidad();
        
      
        universidad.setCodigo(command.codigo().toUpperCase());
        universidad.setNombre(command.nombre());
        universidad.setRuc(command.ruc());
        universidad.setTipo(command.tipo() != null ? command.tipo().toUpperCase() : "PRIVADA");
        universidad.setPlan(command.plan() != null ? command.plan().toUpperCase() : "FREE");
        universidad.setDominio(command.dominio() != null ? command.dominio().toLowerCase() : null);
        universidad.setWebsite(command.website());
        universidad.setMaxEstudiantes(command.maxEstudiantes());
        universidad.setMaxDocentes(command.maxDocentes());
        
        
        universidad.setEstado("ACTIVA");
        universidad.setTotalEstudiantes(0);
        universidad.setTotalDocentes(0);
        
        
        repository.persist(universidad);
        
        return universidad;
    }
    
    /**
     * Valida que el código no exista en otra universidad activa.
     */
    private void validarCodigoUnico(String codigo) {
        if (repository.existsByCodigo(codigo.toUpperCase())) {
            throw new CodigoDuplicadoException(codigo);
        }
    }
    
    /**
     * Valida que el RUC no exista en otra universidad activa.
     */
    private void validarRucUnico(String ruc) {
        if (repository.existsByRuc(ruc)) {
            throw new RucDuplicadoException(ruc);
        }
    }
}
