package upeu.edu.pe.assessment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.assessment.domain.commands.ActualizarCriterioCommand;
import upeu.edu.pe.assessment.domain.entities.EvaluacionCriterio;
import upeu.edu.pe.assessment.domain.repositories.EvaluacionCriterioRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Actualizar un criterio de evaluación existente.
 * 
 * Reglas de negocio:
 * - El criterio debe existir
 * - El nombre no puede estar duplicado (excepto el mismo criterio)
 * - La nota mínima aprobatoria no puede ser mayor a la nota máxima
 * - El nuevo peso total no puede exceder 100%
 * 
 * @author Sistema UPeU
 */
@ApplicationScoped
public class ActualizarCriterioUseCase {

    @Inject
    EvaluacionCriterioRepository criterioRepository;

    /**
     * Ejecuta el caso de uso para actualizar un criterio.
     * 
     * @param command Datos actualizados del criterio
     * @return El criterio actualizado
     * @throws NotFoundException si el criterio no existe
     * @throws BusinessException si se viola alguna regla de negocio
     */
    @Transactional
    public EvaluacionCriterio execute(ActualizarCriterioCommand command) {

        // 1. Buscar el criterio existente
        EvaluacionCriterio criterio = criterioRepository.findByIdOptional(command.criterioId())
                .orElseThrow(() -> new NotFoundException(
                        "Criterio de evaluación no encontrado con ID: " + command.criterioId()));

        // 2. Validar nombre único (excluyendo el mismo criterio)
        validarNombreUnico(command.nombre(), command.seccionId(), command.criterioId());

        // 3. Validar coherencia de notas
        validarNotas(command.notaMaxima(), command.notaMinimaAprobatoria());

        // 4. Si cambia el peso, validar que el total no exceda 100%
        if (!criterio.getPeso().equals(command.peso())) {
            validarPesoTotal(command.seccionId(), command.peso(), criterio.getPeso());
        }

        // 5. Actualizar la entidad
        criterio.setNombre(command.nombre());
        criterio.setPeso(command.peso());
        criterio.setTipoEvaluacion(command.tipoEvaluacion() != null ? command.tipoEvaluacion().toUpperCase()
                : criterio.getTipoEvaluacion());
        criterio.setNotaMaxima(command.notaMaxima() != null ? command.notaMaxima() : criterio.getNotaMaxima());
        criterio.setNotaMinimaAprobatoria(command.notaMinimaAprobatoria() != null ? command.notaMinimaAprobatoria()
                : criterio.getNotaMinimaAprobatoria());
        criterio.setEsRecuperable(
                command.esRecuperable() != null ? command.esRecuperable() : criterio.getEsRecuperable());
        criterio.setDescripcion(command.descripcion());

        if (command.orden() != null) {
            criterio.setOrden(command.orden());
        }
        if (command.estado() != null) {
            criterio.setEstado(command.estado().toUpperCase());
        }

        // 6. Persistir
        criterioRepository.persist(criterio);

        return criterio;
    }

    /**
     * Valida que el nombre no exista en otro criterio de la misma sección.
     */
    private void validarNombreUnico(String nombre, Long seccionId, Long criterioIdActual) {
        criterioRepository.findByNombreAndSeccion(nombre, seccionId)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(criterioIdActual)) {
                        throw new BusinessException(
                                "Ya existe otro criterio con el nombre '" + nombre + "' en esta sección");
                    }
                });
    }

    /**
     * Valida que la nota mínima aprobatoria no sea mayor a la nota máxima.
     */
    private void validarNotas(Integer notaMaxima, Integer notaMinima) {
        if (notaMinima != null && notaMaxima != null && notaMinima > notaMaxima) {
            throw new BusinessException(
                    "La nota mínima aprobatoria (" + notaMinima + ") no puede ser mayor a la nota máxima (" + notaMaxima
                            + ")");
        }
    }

    /**
     * Valida que el peso total no exceda 100% considerando el cambio de peso.
     */
    private void validarPesoTotal(Long seccionId, Integer pesoNuevo, Integer pesoAnterior) {
        Integer pesoActual = criterioRepository.sumPesoBySeccion(seccionId);
        int pesoSinEste = (pesoActual != null ? pesoActual : 0) - pesoAnterior;
        int total = pesoSinEste + pesoNuevo;

        if (total > 100) {
            throw new BusinessException(
                    String.format(
                            "El peso total no puede exceder 100%%. Peso actual sin este criterio: %d%%, nuevo peso: %d%% (total: %d%%)",
                            pesoSinEste, pesoNuevo, total));
        }
    }
}
