package upeu.edu.pe.assessment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.assessment.domain.commands.CrearCriterioCommand;
import upeu.edu.pe.assessment.domain.entities.EvaluacionCriterio;
import upeu.edu.pe.assessment.domain.repositories.EvaluacionCriterioRepository;
import upeu.edu.pe.enrollment.domain.entities.CursoOfertado;
import upeu.edu.pe.enrollment.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Crear un nuevo criterio de evaluación para un curso ofertado.
 * 
 * Reglas de negocio:
 * - El curso ofertado debe existir y estar activo
 * - El nombre del criterio no puede estar duplicado en la sección
 * - La nota mínima aprobatoria no puede ser mayor a la nota máxima
 * - El peso total de todos los criterios no puede exceder 100%
 * 
 * @author Sistema UPeU
 */
@ApplicationScoped
public class CrearCriterioUseCase {

    @Inject
    EvaluacionCriterioRepository criterioRepository;

    @Inject
    CursoOfertadoRepository cursoOfertadoRepository;

    /**
     * Ejecuta el caso de uso para crear un criterio de evaluación.
     * 
     * @param command Datos del criterio a crear
     * @return El criterio creado
     * @throws NotFoundException si el curso ofertado no existe
     * @throws BusinessException si se viola alguna regla de negocio
     */
    @Transactional
    public EvaluacionCriterio execute(CrearCriterioCommand command) {

        // 1. Validar que el curso ofertado existe
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(command.seccionId())
                .orElseThrow(() -> new NotFoundException(
                        "Curso ofertado no encontrado con ID: " + command.seccionId()));

        // 2. Validar nombre único en la sección
        validarNombreUnico(command.nombre(), command.seccionId());

        // 3. Validar coherencia de notas
        validarNotas(command.notaMaxima(), command.notaMinimaAprobatoria());

        // 4. Validar que el peso total no exceda 100%
        validarPesoTotal(command.seccionId(), command.peso());

        // 5. Crear la entidad
        EvaluacionCriterio criterio = new EvaluacionCriterio();
        criterio.setCursoOfertado(cursoOfertado);
        criterio.setNombre(command.nombre());
        criterio.setPeso(command.peso());
        criterio.setTipoEvaluacion(command.tipoEvaluacion().toUpperCase());
        criterio.setNotaMaxima(command.notaMaxima() != null ? command.notaMaxima() : 20);
        criterio.setNotaMinimaAprobatoria(
                command.notaMinimaAprobatoria() != null ? command.notaMinimaAprobatoria() : 11);
        criterio.setEsRecuperable(command.esRecuperable() != null ? command.esRecuperable() : false);
        criterio.setDescripcion(command.descripcion());
        criterio.setOrden(criterioRepository.getNextOrden(command.seccionId()));
        criterio.setEstado("ACTIVO");

        // 6. Persistir
        criterioRepository.persist(criterio);

        return criterio;
    }

    /**
     * Valida que el nombre del criterio no exista en la sección.
     */
    private void validarNombreUnico(String nombre, Long seccionId) {
        criterioRepository.findByNombreAndSeccion(nombre, seccionId)
                .ifPresent(existing -> {
                    throw new BusinessException(
                            "Ya existe un criterio con el nombre '" + nombre + "' en esta sección");
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
     * Valida que el peso total de los criterios no exceda 100%.
     */
    private void validarPesoTotal(Long seccionId, Integer pesoNuevo) {
        Integer pesoActual = criterioRepository.sumPesoBySeccion(seccionId);
        int total = (pesoActual != null ? pesoActual : 0) + pesoNuevo;

        if (total > 100) {
            throw new BusinessException(
                    String.format(
                            "El peso total no puede exceder 100%%. Peso actual: %d%%, intentando agregar: %d%% (total: %d%%)",
                            pesoActual != null ? pesoActual : 0, pesoNuevo, total));
        }
    }
}
