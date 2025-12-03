package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.commands.CrearEstudianteCommand;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.entities.ProgramaAcademico;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.exceptions.*;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.repositories.PersonaRepository;
import upeu.edu.pe.academic.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;

/**
 * Caso de uso: Crear un nuevo estudiante.
 * 
 * Responsabilidades:
 * - Validar que la persona exista
 * - Validar que la persona no sea ya estudiante
 * - Validar que el programa académico exista y esté activo
 * - Validar límites de estudiantes de la universidad
 * - Validar que el código de estudiante no esté duplicado
 * - Validar que el ciclo actual sea válido
 * - Crear y persistir el estudiante
 * - Incrementar contador de estudiantes en la universidad
 */
@ApplicationScoped
public class CrearEstudianteUseCase {
    
    @Inject
    EstudianteRepository estudianteRepository;
    
    @Inject
    PersonaRepository personaRepository;
    
    @Inject
    ProgramaAcademicoRepository programaAcademicoRepository;
    
    @Inject
    UniversidadRepository universidadRepository;
    
    @Inject
    ValidarLimitesUniversidadUseCase validarLimitesUseCase;
    
    /**
     * Ejecuta el caso de uso de creación de estudiante.
     */
    public Estudiante execute(CrearEstudianteCommand command) {
        //  Validar que exista la universidad y tenga cupo
        validarLimitesUseCase.validarAgregarEstudiante(command.universidadId());
        
        Universidad universidad = universidadRepository.findByIdOptional(command.universidadId())
            .orElseThrow(() -> new UniversidadNoEncontradaException(command.universidadId()));
        
        // Validar que exista la persona
        Persona persona = personaRepository.findByIdOptional(command.personaId())
            .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + command.personaId()));
        
        // Validar que la persona no sea ya estudiante en esta universidad
        if (estudianteRepository.existsByPersonaAndUniversidad(command.personaId(), command.universidadId())) {
            throw new PersonaYaEsEstudianteException(command.personaId());
        }
        
        
        ProgramaAcademico programa = programaAcademicoRepository.findByIdOptional(command.programaAcademicoId())
            .filter(p -> p.getActive())
            .orElseThrow(() -> new RuntimeException("Programa académico no encontrado con ID: " + command.programaAcademicoId()));
        
        if (!"ACTIVO".equals(programa.getEstado())) {
            throw new ProgramaAcademicoInactivoException(command.programaAcademicoId());
        }
        
        
        if (estudianteRepository.existsByCodigoEstudiante(command.codigoEstudiante())) {
            throw new CodigoEstudianteDuplicadoException(command.codigoEstudiante());
        }
        
        
        if (command.cicloActual() > programa.getDuracionSemestres()) {
            throw new CicloInvalidoException(command.cicloActual(), programa.getDuracionSemestres());
        }
        
        
        Estudiante estudiante = new Estudiante();
        estudiante.setPersona(persona);
        estudiante.setUniversidad(universidad);
        estudiante.setProgramaAcademico(programa);
        estudiante.setCodigoEstudiante(command.codigoEstudiante().toUpperCase());
        estudiante.setFechaIngreso(command.fechaIngreso());
        estudiante.setCicloActual(command.cicloActual());
        estudiante.setModalidadIngreso(
            command.modalidadIngreso() != null ? command.modalidadIngreso().toUpperCase() : "EXAMEN"
        );
        estudiante.setTipoEstudiante(
            command.tipoEstudiante() != null ? command.tipoEstudiante().toUpperCase() : "REGULAR"
        );
        
    
        estudiante.setEstadoAcademico("ACTIVO");
        estudiante.setCreditosAprobados(0);
        estudiante.setCreditosCursando(0);
        estudiante.setCreditosObligatoriosAprobados(0);
        estudiante.setCreditosElectivosAprobados(0);
        
        
        estudianteRepository.persist(estudiante);
        

        validarLimitesUseCase.incrementarEstudiantes(command.universidadId());
        
        return estudiante;
    }
}
