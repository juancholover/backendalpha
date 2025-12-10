package upeu.edu.pe.assessment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.assessment.domain.commands.RegistrarNotaRecuperacionCommand;
import upeu.edu.pe.assessment.domain.entities.EvaluacionCriterio;
import upeu.edu.pe.assessment.domain.entities.EvaluacionNota;
import upeu.edu.pe.assessment.domain.repositories.EvaluacionNotaRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;

/**
 * Caso de Uso: Registrar una nota de recuperación para una evaluación.
 * 
 * Reglas de negocio:
 * - La nota original debe existir
 * - El criterio debe ser recuperable
 * - La nota de recuperación debe estar en el rango [0, notaMaxima]
 * - La nota final será el máximo entre la nota original y la recuperación
 * 
 * @author Sistema UPeU
 */
@ApplicationScoped
public class RegistrarNotaRecuperacionUseCase {

    @Inject
    EvaluacionNotaRepository notaRepository;

    /**
     * Ejecuta el caso de uso para registrar una nota de recuperación.
     * 
     * @param command Datos de la nota de recuperación
     * @return La nota actualizada con la recuperación
     * @throws NotFoundException si la nota no existe
     * @throws BusinessException si el criterio no es recuperable o la nota está
     *                           fuera de rango
     */
    @Transactional
    public EvaluacionNota execute(RegistrarNotaRecuperacionCommand command) {

        // 1. Buscar la nota existente
        EvaluacionNota nota = notaRepository.findByIdOptional(command.notaId())
                .orElseThrow(() -> new NotFoundException(
                        "Nota de evaluación no encontrada con ID: " + command.notaId()));

        // 2. Validar que el criterio sea recuperable
        EvaluacionCriterio criterio = nota.getCriterio();
        if (!Boolean.TRUE.equals(criterio.getEsRecuperable())) {
            throw new BusinessException(
                    String.format("El criterio '%s' no permite recuperación.", criterio.getNombre()));
        }

        // 3. Validar el rango de la nota de recuperación
        validarRangoNota(command.notaRecuperacion(), criterio);

        // 4. Registrar la nota de recuperación
        nota.registrarNotaRecuperacion(command.notaRecuperacion());

        // 5. Agregar observación si existe
        if (command.observacion() != null && !command.observacion().isBlank()) {
            String observacionActual = nota.getObservacion() != null ? nota.getObservacion() : "";
            nota.setObservacion(observacionActual + " [RECUPERACIÓN: " + command.observacion() + "]");
        }

        // 6. Persistir
        notaRepository.persist(nota);

        return nota;
    }

    /**
     * Valida que la nota esté en el rango permitido.
     */
    private void validarRangoNota(BigDecimal nota, EvaluacionCriterio criterio) {
        if (nota.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("La nota de recuperación no puede ser negativa.");
        }

        BigDecimal notaMaxima = new BigDecimal(criterio.getNotaMaxima());
        if (nota.compareTo(notaMaxima) > 0) {
            throw new BusinessException(
                    String.format(
                            "La nota de recuperación (%.2f) no puede ser mayor a la nota máxima del criterio (%d).",
                            nota, criterio.getNotaMaxima()));
        }
    }
}
