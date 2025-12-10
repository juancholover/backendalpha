package upeu.edu.pe.people.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.core.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.people.domain.commands.ActualizarEmpleadoCommand;
import upeu.edu.pe.people.domain.entities.Empleado;
import upeu.edu.pe.people.domain.repositories.EmpleadoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Actualizar un empleado existente.
 * 
 * Reglas de negocio:
 * - El empleado debe existir
 * - El código debe ser único (excluyendo el actual)
 * - La fecha de cese no puede ser anterior a la fecha de ingreso
 */
@ApplicationScoped
public class ActualizarEmpleadoUseCase {

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    UnidadOrganizativaRepository unidadRepository;

    @Transactional
    public Empleado execute(ActualizarEmpleadoCommand command) {

        // 1. Buscar empleado
        Empleado empleado = empleadoRepository.findByIdOptional(command.id())
                .orElseThrow(() -> new NotFoundException("Empleado no encontrado con ID: " + command.id()));

        // 2. Validar código único
        if (!empleado.getCodigoEmpleado().equals(command.codigoEmpleado()) &&
                empleadoRepository.existsByCodigoEmpleado(command.codigoEmpleado())) {
            throw new DuplicateResourceException("Empleado", "codigoEmpleado", command.codigoEmpleado());
        }

        // 3. Validar unidad organizativa
        if (command.unidadOrganizativaId() != null) {
            UnidadOrganizativa unidad = unidadRepository.findByIdOptional(command.unidadOrganizativaId())
                    .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada"));
            empleado.setUnidadOrganizativa(unidad);
        }

        // 4. Validar fechas
        if (command.fechaCese() != null && command.fechaIngreso() != null &&
                command.fechaIngreso().isAfter(command.fechaCese())) {
            throw new BusinessException("La fecha de cese no puede ser anterior a la fecha de ingreso");
        }

        // 5. Actualizar
        empleado.setCodigoEmpleado(command.codigoEmpleado());
        empleado.setCargo(command.cargo());
        empleado.setTipoContrato(command.tipoContrato());
        empleado.setFechaIngreso(command.fechaIngreso());
        empleado.setFechaCese(command.fechaCese());
        empleado.setEstadoLaboral(command.estadoLaboral());

        empleadoRepository.persist(empleado);

        return empleado;
    }
}
