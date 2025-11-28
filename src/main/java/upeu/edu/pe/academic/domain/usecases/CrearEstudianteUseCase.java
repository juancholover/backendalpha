package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.entities.ProgramaAcademico;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.repositories.PersonaRepository;
import upeu.edu.pe.academic.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.shared.exceptions.BusinessRuleException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.time.LocalDate;

@ApplicationScoped
public class CrearEstudianteUseCase {

    private final EstudianteRepository estudianteRepository;
    private final PersonaRepository personaRepository;
    private final ProgramaAcademicoRepository programaAcademicoRepository;

    @Inject
    public CrearEstudianteUseCase(EstudianteRepository estudianteRepository,
            PersonaRepository personaRepository,
            ProgramaAcademicoRepository programaAcademicoRepository) {
        this.estudianteRepository = estudianteRepository;
        this.personaRepository = personaRepository;
        this.programaAcademicoRepository = programaAcademicoRepository;
    }

    public Estudiante execute(Long personaId, Long programaAcademicoId, String codigoEstudiante,
            LocalDate fechaIngreso, Integer cicloActual, String modalidadIngreso,
            String tipoEstudiante) {

        // Validar Persona
        Persona persona = personaRepository.findByIdOptional(personaId)
                .filter(Persona::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", personaId));

        if (estudianteRepository.existsByPersona(personaId)) {
            throw new BusinessRuleException("La persona con ID " + personaId + " ya es estudiante");
        }

        // Validar Programa Académico
        ProgramaAcademico programa = programaAcademicoRepository.findByIdOptional(programaAcademicoId)
                .filter(ProgramaAcademico::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramaAcademico", "id", programaAcademicoId));

        if (!"ACTIVO".equals(programa.getEstado())) {
            throw new BusinessRuleException("El programa académico no está activo");
        }

        // Validar duplicados
        if (estudianteRepository.existsByCodigoEstudiante(codigoEstudiante)) {
            throw new DuplicateResourceException("Estudiante", "codigoEstudiante", codigoEstudiante);
        }

        // Validar ciclo
        if (cicloActual != null && cicloActual > programa.getDuracionSemestres()) {
            throw new BusinessRuleException(
                    "El ciclo actual (" + cicloActual + ") no puede ser mayor a la duración del programa (" +
                            programa.getDuracionSemestres() + " semestres)");
        }

        // Crear entidad
        Estudiante estudiante = Estudiante.crear(persona, persona.getUniversidad(), programa,
                codigoEstudiante, fechaIngreso, cicloActual,
                modalidadIngreso, tipoEstudiante);

        estudianteRepository.persist(estudiante);

        return estudiante;
    }
}
