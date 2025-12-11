package upeu.edu.pe.enrollment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.enrollment.domain.commands.CrearPeriodoAcademicoCommand;
import upeu.edu.pe.enrollment.domain.entities.PeriodoAcademico;
import upeu.edu.pe.enrollment.domain.repositories.PeriodoAcademicoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;

@ApplicationScoped
public class CrearPeriodoAcademicoUseCase {

    @Inject
    PeriodoAcademicoRepository periodoRepository;

    @Transactional
    public PeriodoAcademico execute(CrearPeriodoAcademicoCommand command) {
        // 1. Validar código único
        if (periodoRepository.existsByCodigo(command.codigoPeriodo())) {
            throw new BusinessException("Ya existe un período con el código: " + command.codigoPeriodo());
        }

        // 2. Validar fechas
        if (command.fechaFin().isBefore(command.fechaInicio())) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        if (command.fechaFinMatricula() != null && command.fechaInicioMatricula() != null) {
            if (command.fechaFinMatricula().isBefore(command.fechaInicioMatricula())) {
                throw new BusinessException("La fecha fin de matrícula debe ser posterior a la fecha inicio");
            }
        }

        // 3. Si se marca como actual, desmarcar los demás
        if (Boolean.TRUE.equals(command.esActual())) {
            periodoRepository.desmarcarTodosComoActual();
        }

        // 4. Crear periodo
        PeriodoAcademico periodo = new PeriodoAcademico();
        periodo.setCodigoPeriodo(command.codigoPeriodo());
        periodo.setNombre(command.nombre());
        periodo.setAnio(command.anio());
        periodo.setTipoPeriodo(command.tipoPeriodo());
        periodo.setFechaInicio(command.fechaInicio());
        periodo.setFechaFin(command.fechaFin());
        periodo.setFechaInicioMatricula(command.fechaInicioMatricula());
        periodo.setFechaFinMatricula(command.fechaFinMatricula());
        periodo.setEsActual(command.esActual());

        periodoRepository.persist(periodo);
        return periodo;
    }
}
