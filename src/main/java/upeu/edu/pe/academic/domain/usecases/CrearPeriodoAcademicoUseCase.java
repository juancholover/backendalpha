package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.PeriodoAcademico;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.PeriodoAcademicoRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.time.LocalDate;

@ApplicationScoped
public class CrearPeriodoAcademicoUseCase {

    private final PeriodoAcademicoRepository periodoRepository;
    private final UniversidadRepository universidadRepository;

    @Inject
    public CrearPeriodoAcademicoUseCase(PeriodoAcademicoRepository periodoRepository,
            UniversidadRepository universidadRepository) {
        this.periodoRepository = periodoRepository;
        this.universidadRepository = universidadRepository;
    }

    public PeriodoAcademico execute(Long universidadId, String codigoPeriodo, String nombre,
            Integer anio, String tipoPeriodo, Integer numeroPeriodo,
            LocalDate fechaInicio, LocalDate fechaFin,
            LocalDate fechaInicioMatricula, LocalDate fechaFinMatricula,
            LocalDate fechaInicioClases, LocalDate fechaFinClases,
            String descripcion) {

        // Validar Universidad
        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));

        // Validar duplicados
        if (periodoRepository.existsByCodigoPeriodo(codigoPeriodo)) {
            throw new DuplicateResourceException("PeriodoAcademico", "codigoPeriodo", codigoPeriodo);
        }

        // Crear entidad
        PeriodoAcademico periodo = PeriodoAcademico.crear(universidad, codigoPeriodo, nombre,
                anio, tipoPeriodo, numeroPeriodo,
                fechaInicio, fechaFin,
                fechaInicioMatricula, fechaFinMatricula,
                fechaInicioClases, fechaFinClases,
                descripcion);

        periodoRepository.persist(periodo);

        return periodo;
    }
}
