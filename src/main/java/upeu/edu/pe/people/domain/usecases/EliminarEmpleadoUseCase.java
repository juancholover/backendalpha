package upeu.edu.pe.people.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.people.domain.entities.Empleado;
import upeu.edu.pe.people.domain.repositories.EmpleadoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.time.LocalDate;

/**
 * Caso de Uso: Eliminar (soft delete) un empleado.
 */
@ApplicationScoped
public class EliminarEmpleadoUseCase {

    @Inject
    EmpleadoRepository empleadoRepository;

    @Transactional
    public void execute(Long empleadoId) {
        Empleado empleado = empleadoRepository.findByIdOptional(empleadoId)
                .orElseThrow(() -> new NotFoundException("Empleado no encontrado con ID: " + empleadoId));

        empleado.setActive(false);
        empleado.setEstadoLaboral("CESADO");
        if (empleado.getFechaCese() == null) {
            empleado.setFechaCese(LocalDate.now());
        }

        empleadoRepository.persist(empleado);
    }
}
