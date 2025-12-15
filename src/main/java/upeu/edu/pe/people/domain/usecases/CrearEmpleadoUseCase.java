package upeu.edu.pe.people.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.core.domain.repositories.PersonaRepository;
import upeu.edu.pe.core.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.people.domain.commands.CrearEmpleadoCommand;
import upeu.edu.pe.people.domain.entities.Empleado;
import upeu.edu.pe.people.domain.repositories.EmpleadoRepository;
import upeu.edu.pe.security.domain.services.RoleSyncService;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.time.LocalDate;

/**
 * Caso de Uso: Crear un nuevo empleado.
 * 
 * Reglas de negocio:
 * - La persona debe existir
 * - La persona debe ser mayor de edad (18+)
 * - La persona no puede ser empleado ya
 * - El código de empleado debe ser único
 * - La unidad organizativa debe existir (si se proporciona)
 */
@ApplicationScoped
public class CrearEmpleadoUseCase {

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    RoleSyncService roleSyncService;

    @Inject
    PersonaRepository personaRepository;

    @Inject
    UnidadOrganizativaRepository unidadRepository;

    @Transactional
    public Empleado execute(CrearEmpleadoCommand command) {

        // 1. Validar que la persona existe
        Persona persona = personaRepository.findByIdOptional(command.personaId())
                .orElseThrow(() -> new NotFoundException("Persona no encontrada con ID: " + command.personaId()));

        // 2. Validar mayoría de edad
        if (persona.getFechaNacimiento() != null) {
            int edad = LocalDate.now().getYear() - persona.getFechaNacimiento().getYear();
            if (edad < 18) {
                throw new BusinessException("El empleado debe ser mayor de edad (18 años o más)");
            }
        }

        // 3. Validar que no sea empleado ya
        if (empleadoRepository.existsByPersona(command.personaId())) {
            throw new DuplicateResourceException("La persona con ID " + command.personaId() + " ya es empleado");
        }

        // 4. Validar código único
        if (empleadoRepository.existsByCodigoEmpleado(command.codigoEmpleado())) {
            throw new DuplicateResourceException("Empleado", "codigoEmpleado", command.codigoEmpleado());
        }

        // 5. Validar unidad organizativa
        UnidadOrganizativa unidad = null;
        if (command.unidadOrganizativaId() != null) {
            unidad = unidadRepository.findByIdOptional(command.unidadOrganizativaId())
                    .orElseThrow(() -> new NotFoundException(
                            "Unidad organizativa no encontrada con ID: " + command.unidadOrganizativaId()));
        }

        // 6. Crear entidad
        Empleado empleado = new Empleado();
        empleado.setPersona(persona);
        empleado.setUnidadOrganizativa(unidad);
        empleado.setCodigoEmpleado(command.codigoEmpleado());
        empleado.setCargo(command.cargo());
        empleado.setTipoContrato(command.tipoContrato());
        empleado.setFechaIngreso(command.fechaIngreso());
        empleado.setEstadoLaboral(command.estadoLaboral() != null ? command.estadoLaboral() : "ACTIVO");

        // 7. Persistir
        empleadoRepository.persist(empleado);

        // Sincronizar roles (crea usuario si no existe)
        roleSyncService.syncAllRolesForPersona(persona);

        return empleado;
    }
}
