package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.commands.ActualizarEstudianteCommand;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.ProgramaAcademico;
import upeu.edu.pe.academic.domain.exceptions.*;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.repositories.ProgramaAcademicoRepository;

/**
 * Caso de uso: Actualizar un estudiante existente.
 * 
 * Solo actualiza los campos proporcionados (no nulos).
 */
@ApplicationScoped
public class ActualizarEstudianteUseCase {
    
    @Inject
    EstudianteRepository estudianteRepository;
    
    @Inject
    ProgramaAcademicoRepository programaAcademicoRepository;
    
    /**
     * Ejecuta la actualización del estudiante.
     */
    public Estudiante execute(ActualizarEstudianteCommand command) {
        // 1. Buscar el estudiante
        Estudiante estudiante = estudianteRepository.findByIdOptional(command.id())
            .filter(e -> e.getActive())
            .orElseThrow(() -> new EstudianteNoEncontradoException(command.id()));
        
        // 2. Actualizar programa académico si cambió
        if (command.programaAcademicoId() != null && 
            !estudiante.getProgramaAcademico().getId().equals(command.programaAcademicoId())) {
            
            ProgramaAcademico nuevoPrograma = programaAcademicoRepository.findByIdOptional(command.programaAcademicoId())
                .filter(p -> p.getActive())
                .orElseThrow(() -> new RuntimeException("Programa académico no encontrado"));
            
            if (!"ACTIVO".equals(nuevoPrograma.getEstado())) {
                throw new ProgramaAcademicoInactivoException(command.programaAcademicoId());
            }
            
            estudiante.setProgramaAcademico(nuevoPrograma);
        }
        
        // 3. Validar código de estudiante si cambió
        if (command.codigoEstudiante() != null && 
            !estudiante.getCodigoEstudiante().equals(command.codigoEstudiante())) {
            
            if (estudianteRepository.existsByCodigoEstudianteAndIdNot(command.codigoEstudiante(), command.id())) {
                throw new CodigoEstudianteDuplicadoException(command.codigoEstudiante());
            }
            
            estudiante.setCodigoEstudiante(command.codigoEstudiante().toUpperCase());
        }
        
        // 4. Validar ciclo actual si cambió
        if (command.cicloActual() != null) {
            if (command.cicloActual() > estudiante.getProgramaAcademico().getDuracionSemestres()) {
                throw new CicloInvalidoException(
                    command.cicloActual(), 
                    estudiante.getProgramaAcademico().getDuracionSemestres()
                );
            }
            estudiante.setCicloActual(command.cicloActual());
        }
        
        // 5. Actualizar campos opcionales
        if (command.creditosAprobados() != null) {
            estudiante.setCreditosAprobados(command.creditosAprobados());
        }
        
        if (command.creditosCursando() != null) {
            estudiante.setCreditosCursando(command.creditosCursando());
        }
        
        if (command.creditosObligatoriosAprobados() != null) {
            estudiante.setCreditosObligatoriosAprobados(command.creditosObligatoriosAprobados());
        }
        
        if (command.creditosElectivosAprobados() != null) {
            estudiante.setCreditosElectivosAprobados(command.creditosElectivosAprobados());
        }
        
        if (command.promedioPonderado() != null) {
            estudiante.setPromedioPonderado(command.promedioPonderado());
        }
        
        if (command.estadoAcademico() != null) {
            estudiante.setEstadoAcademico(command.estadoAcademico().toUpperCase());
        }
        
        if (command.tipoEstudiante() != null) {
            estudiante.setTipoEstudiante(command.tipoEstudiante().toUpperCase());
        }
        
        // 6. Persistir cambios (Panache detecta cambios automáticamente en transacción)
        return estudiante;
    }
}
