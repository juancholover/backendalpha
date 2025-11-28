package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.ProgramaAcademico;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.shared.exceptions.BusinessRuleException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.time.LocalDate;

@ApplicationScoped
public class ActualizarEstudianteUseCase {

    private final EstudianteRepository estudianteRepository;
    private final ProgramaAcademicoRepository programaAcademicoRepository;

    @Inject
    public ActualizarEstudianteUseCase(EstudianteRepository estudianteRepository,
            ProgramaAcademicoRepository programaAcademicoRepository) {
        this.estudianteRepository = estudianteRepository;
        this.programaAcademicoRepository = programaAcademicoRepository;
    }

    public Estudiante execute(Long id, Long personaId, Long programaAcademicoId, String codigoEstudiante,
            LocalDate fechaIngreso, Integer cicloActual, String modalidadIngreso,
            String tipoEstudiante) {

        Estudiante estudiante = estudianteRepository.findByIdOptional(id)
                .filter(Estudiante::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", id));

        // Validar cambio de persona (prohibido)
        if (!estudiante.getPersona().getId().equals(personaId)) {
            throw new BusinessRuleException("No se puede cambiar la persona asociada a un estudiante");
        }

        // Validar cambio de programa
        if (!estudiante.getProgramaAcademico().getId().equals(programaAcademicoId)) {
            ProgramaAcademico nuevoPrograma = programaAcademicoRepository.findByIdOptional(programaAcademicoId)
                    .filter(ProgramaAcademico::getActive)
                    .orElseThrow(() -> new ResourceNotFoundException("ProgramaAcademico", "id", programaAcademicoId));

            if (!"ACTIVO".equals(nuevoPrograma.getEstado())) {
                throw new BusinessRuleException("El programa académico no está activo");
            }
            estudiante.setProgramaAcademico(nuevoPrograma);
        }

        // Validar duplicados de código
        if (!estudiante.getCodigoEstudiante().equals(codigoEstudiante) &&
                estudianteRepository.existsByCodigoEstudianteAndIdNot(codigoEstudiante, id)) {
            throw new DuplicateResourceException("Estudiante", "codigoEstudiante", codigoEstudiante);
        }

        // Validar ciclo
        if (cicloActual != null && cicloActual > estudiante.getProgramaAcademico().getDuracionSemestres()) {
            throw new BusinessRuleException(
                    "El ciclo actual (" + cicloActual + ") no puede ser mayor a la duración del programa (" +
                            estudiante.getProgramaAcademico().getDuracionSemestres() + " semestres)");
        }

        // Actualizar campos
        estudiante.setCodigoEstudiante(codigoEstudiante);
        estudiante.setFechaIngreso(fechaIngreso);
        estudiante.setCicloActual(cicloActual);
        estudiante.setModalidadIngreso(modalidadIngreso);
        if (tipoEstudiante != null) {
            estudiante.setTipoEstudiante(tipoEstudiante);
        }

        estudianteRepository.persist(estudiante);

        return estudiante;
    }
}
