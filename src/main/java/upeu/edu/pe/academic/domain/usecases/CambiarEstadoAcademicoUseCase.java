package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.commands.CambiarEstadoAcademicoCommand;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.exceptions.EstudianteNoEncontradoException;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;

/**
 * Caso de uso: Cambiar el estado académico de un estudiante.
 * 
 * Maneja transiciones de estado como:
 * - ACTIVO → SUSPENDIDO
 * - ACTIVO → RETIRADO
 * - ACTIVO → EGRESADO
 * - SUSPENDIDO → ACTIVO (reincorporación)
 */
@ApplicationScoped
public class CambiarEstadoAcademicoUseCase {
    
    @Inject
    EstudianteRepository estudianteRepository;
    
    /**
     * Ejecuta el cambio de estado académico.
     */
    public Estudiante execute(CambiarEstadoAcademicoCommand command) {
        // 1. Buscar el estudiante
        Estudiante estudiante = estudianteRepository.findByIdOptional(command.estudianteId())
            .filter(e -> e.getActive())
            .orElseThrow(() -> new EstudianteNoEncontradoException(command.estudianteId()));
        
        // 2. Cambiar el estado
        String estadoAnterior = estudiante.getEstadoAcademico();
        estudiante.setEstadoAcademico(command.nuevoEstado().toUpperCase());
        
        // 3. Lógica adicional según el nuevo estado
        switch (command.nuevoEstado().toUpperCase()) {
            case "EGRESADO":
                // El estudiante ha completado todos los cursos pero aún no se gradúa
                estudiante.setTipoEstudiante("EGRESADO");
                break;
                
            case "GRADUADO":
                // El estudiante se ha graduado oficialmente
                estudiante.setTipoEstudiante("GRADUADO");
                break;
                
            case "RETIRADO":
                // Marcar créditos cursando como 0 al retirarse
                estudiante.setCreditosCursando(0);
                break;
                
            case "SUSPENDIDO":
                // Suspendido temporalmente, mantener créditos cursando
                break;
                
            case "ACTIVO":
                // Reincorporación: validar que venga de un estado válido
                if ("GRADUADO".equals(estadoAnterior) || "RETIRADO".equals(estadoAnterior)) {
                    throw new IllegalStateException(
                        "No se puede reactivar un estudiante en estado " + estadoAnterior
                    );
                }
                break;
        }
        
        // 4. Persistir cambios
        return estudiante;
    }
}
