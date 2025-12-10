package upeu.edu.pe.assessment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.assessment.domain.commands.RegistrarNotaCommand;
import upeu.edu.pe.assessment.domain.entities.EvaluacionCriterio;
import upeu.edu.pe.assessment.domain.entities.EvaluacionNota;
import upeu.edu.pe.assessment.domain.repositories.EvaluacionCriterioRepository;
import upeu.edu.pe.assessment.domain.repositories.EvaluacionNotaRepository;
import upeu.edu.pe.enrollment.domain.entities.Matricula;
import upeu.edu.pe.enrollment.domain.repositories.MatriculaRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Caso de Uso: Registrar una nota de evaluación para un estudiante.
 * 
 * Reglas de negocio:
 * - La matrícula debe existir y estar activa (estado MATRICULADO)
 * - El criterio debe existir y estar activo
 * - La nota debe estar en el rango [0, notaMaxima del criterio]
 * - No puede existir una nota duplicada para la misma matrícula y criterio
 * 
 * @author Sistema UPeU
 */
@ApplicationScoped
public class RegistrarNotaUseCase {

    @Inject
    EvaluacionNotaRepository notaRepository;

    @Inject
    EvaluacionCriterioRepository criterioRepository;

    @Inject
    MatriculaRepository matriculaRepository;

    /**
     * Ejecuta el caso de uso para registrar una nota.
     * 
     * @param command Datos de la nota a registrar
     * @return La nota registrada
     * @throws NotFoundException si la matrícula o el criterio no existen
     * @throws BusinessException si se viola alguna regla de negocio
     */
    @Transactional
    public EvaluacionNota execute(RegistrarNotaCommand command) {

        // 1. Validar y obtener la matrícula
        Matricula matricula = matriculaRepository.findByIdOptional(command.matriculaId())
                .orElseThrow(() -> new NotFoundException(
                        "Matrícula no encontrada con ID: " + command.matriculaId()));

        validarMatriculaActiva(matricula);

        // 2. Validar y obtener el criterio
        EvaluacionCriterio criterio = criterioRepository.findByIdOptional(command.criterioId())
                .orElseThrow(() -> new NotFoundException(
                        "Criterio de evaluación no encontrado con ID: " + command.criterioId()));

        validarCriterioActivo(criterio);

        // 3. Validar que el criterio pertenezca al curso de la matrícula
        validarCriterioPerteneceCurso(matricula, criterio);

        // 4. Validar que la nota esté en el rango permitido
        validarRangoNota(command.nota(), criterio);

        // 5. Verificar que no exista nota duplicada
        if (notaRepository.existsByMatriculaAndCriterio(command.matriculaId(), command.criterioId())) {
            throw new BusinessException(
                    String.format("Ya existe una nota registrada para el estudiante en el criterio '%s'. " +
                            "Use la opción de actualizar nota.", criterio.getNombre()));
        }

        // 6. Crear y persistir la nota
        EvaluacionNota nota = new EvaluacionNota();
        nota.setMatricula(matricula);
        nota.setCriterio(criterio);
        nota.setNota(command.nota());
        nota.setNotaFinal(command.nota());
        nota.setObservacion(command.observacion());
        nota.setFechaEvaluacion(command.fechaEvaluacion() != null ? command.fechaEvaluacion() : LocalDateTime.now());
        nota.setFechaCalificacion(LocalDateTime.now());
        nota.setEstado("CALIFICADO");

        notaRepository.persist(nota);

        return nota;
    }

    /**
     * Valida que la matrícula esté activa.
     */
    private void validarMatriculaActiva(Matricula matricula) {
        if (!"MATRICULADO".equals(matricula.getEstadoMatricula())) {
            throw new BusinessException(
                    String.format("No se puede registrar nota: la matrícula está en estado '%s'. " +
                            "Solo se pueden registrar notas en matrículas activas (MATRICULADO).",
                            matricula.getEstadoMatricula()));
        }
    }

    /**
     * Valida que el criterio esté activo.
     */
    private void validarCriterioActivo(EvaluacionCriterio criterio) {
        if (!"ACTIVO".equals(criterio.getEstado())) {
            throw new BusinessException(
                    String.format("No se puede registrar nota: el criterio '%s' está en estado '%s'.",
                            criterio.getNombre(), criterio.getEstado()));
        }
    }

    /**
     * Valida que el criterio pertenezca al mismo curso ofertado de la matrícula.
     */
    private void validarCriterioPerteneceCurso(Matricula matricula, EvaluacionCriterio criterio) {
        Long cursoMatricula = matricula.getCursoOfertado().getId();
        Long cursoCriterio = criterio.getCursoOfertado().getId();

        if (!cursoMatricula.equals(cursoCriterio)) {
            throw new BusinessException(
                    "El criterio de evaluación no pertenece al curso en el que está matriculado el estudiante.");
        }
    }

    /**
     * Valida que la nota esté en el rango permitido [0, notaMaxima].
     */
    private void validarRangoNota(BigDecimal nota, EvaluacionCriterio criterio) {
        if (nota.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("La nota no puede ser negativa.");
        }

        BigDecimal notaMaxima = new BigDecimal(criterio.getNotaMaxima());
        if (nota.compareTo(notaMaxima) > 0) {
            throw new BusinessException(
                    String.format("La nota (%.2f) no puede ser mayor a la nota máxima del criterio (%d).",
                            nota, criterio.getNotaMaxima()));
        }
    }
}
