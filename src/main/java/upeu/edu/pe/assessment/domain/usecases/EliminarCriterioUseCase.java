package upeu.edu.pe.assessment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.assessment.domain.entities.EvaluacionCriterio;
import upeu.edu.pe.assessment.domain.repositories.EvaluacionCriterioRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Eliminar (soft delete) un criterio de evaluación.
 * 
 * Reglas de negocio:
 * - El criterio debe existir
 * - No se puede eliminar si tiene notas registradas
 * 
 * @author Sistema UPeU
 */
@ApplicationScoped
public class EliminarCriterioUseCase {

    @Inject
    EvaluacionCriterioRepository criterioRepository;

    /**
     * Ejecuta el caso de uso para eliminar un criterio.
     * 
     * @param criterioId ID del criterio a eliminar
     * @throws NotFoundException si el criterio no existe
     * @throws BusinessException si el criterio tiene notas registradas
     */
    @Transactional
    public void execute(Long criterioId) {

        // 1. Buscar el criterio
        EvaluacionCriterio criterio = criterioRepository.findByIdOptional(criterioId)
                .orElseThrow(() -> new NotFoundException(
                        "Criterio de evaluación no encontrado con ID: " + criterioId));

        // 2. Validar que no tenga notas registradas
        if (criterio.getEvaluacionNotas() != null && !criterio.getEvaluacionNotas().isEmpty()) {
            int cantidadNotas = criterio.getEvaluacionNotas().size();
            throw new BusinessException(
                    String.format("No se puede eliminar el criterio '%s' porque tiene %d nota(s) registrada(s). " +
                            "Primero debe eliminar las notas asociadas.",
                            criterio.getNombre(), cantidadNotas));
        }

        // 3. Soft delete
        criterio.setActive(false);
        criterio.setEstado("ELIMINADO");
        criterioRepository.persist(criterio);
    }
}
