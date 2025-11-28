package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.PeriodoAcademico;
import upeu.edu.pe.academic.domain.repositories.PeriodoAcademicoRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.time.LocalDate;

@ApplicationScoped
public class ActualizarPeriodoAcademicoUseCase {

    private final PeriodoAcademicoRepository repository;

    @Inject
    public ActualizarPeriodoAcademicoUseCase(PeriodoAcademicoRepository repository) {
        this.repository = repository;
    }

    public PeriodoAcademico execute(Long id, String codigoPeriodo, String nombre,
            Integer anio, String tipoPeriodo, Integer numeroPeriodo,
            LocalDate fechaInicio, LocalDate fechaFin,
            LocalDate fechaInicioMatricula, LocalDate fechaFinMatricula,
            LocalDate fechaInicioClases, LocalDate fechaFinClases,
            String estado, Boolean esActual, String descripcion) {

        PeriodoAcademico periodo = repository.findByIdOptional(id)
                .filter(PeriodoAcademico::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("PeriodoAcademico", "id", id));

        // Validar duplicados de código
        if (!periodo.getCodigoPeriodo().equals(codigoPeriodo) &&
                repository.existsByCodigoPeriodoAndIdNot(codigoPeriodo, id)) {
            throw new DuplicateResourceException("PeriodoAcademico", "codigoPeriodo", codigoPeriodo);
        }

        // Actualizar campos
        periodo.setCodigoPeriodo(codigoPeriodo);
        periodo.setNombre(nombre);
        periodo.setAnio(anio);
        periodo.setTipoPeriodo(tipoPeriodo);
        periodo.setNumeroPeriodo(numeroPeriodo);
        periodo.setFechaInicio(fechaInicio);
        periodo.setFechaFin(fechaFin);
        periodo.setFechaInicioMatricula(fechaInicioMatricula);
        periodo.setFechaFinMatricula(fechaFinMatricula);
        periodo.setFechaInicioClases(fechaInicioClases);
        periodo.setFechaFinClases(fechaFinClases);
        periodo.setDescripcion(descripcion);

        if (estado != null) {
            periodo.setEstado(estado);
        }

        if (esActual != null) {
            if (esActual && !periodo.getEsActual()) {
                // Si se marca como actual, desactivar otros
                repository.desactivarOtrosPeriodosActuales(periodo.getUniversidad().getId(), id);
            }
            periodo.setEsActual(esActual);
        }

        repository.persist(periodo);

        return periodo;
    }
}
