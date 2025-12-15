package upeu.edu.pe.people.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.people.domain.commands.CrearProfesorCommand;
import upeu.edu.pe.people.domain.entities.Empleado;
import upeu.edu.pe.people.domain.entities.Profesor;
import upeu.edu.pe.people.domain.repositories.EmpleadoRepository;
import upeu.edu.pe.people.domain.repositories.ProfesorRepository;
import upeu.edu.pe.security.domain.services.RoleSyncService;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Crear un nuevo profesor.
 * 
 * Reglas de negocio:
 * - El empleado debe existir y tener persona activa
 * - El empleado no puede ser profesor ya
 * - Validar grado académico según categoría docente
 */
@ApplicationScoped
public class CrearProfesorUseCase {

    @Inject
    ProfesorRepository profesorRepository;

    @Inject
    RoleSyncService roleSyncService;

    @Inject
    EmpleadoRepository empleadoRepository;

    @Transactional
    public Profesor execute(CrearProfesorCommand command) {

        // 1. Validar que el empleado existe
        Empleado empleado = empleadoRepository.findByIdOptional(command.empleadoId())
                .orElseThrow(() -> new NotFoundException("Empleado no encontrado con ID: " + command.empleadoId()));

        // 2. Validar que tiene persona activa
        if (empleado.getPersona() == null || !empleado.getPersona().getActive()) {
            throw new BusinessException("El empleado no tiene una persona activa asociada");
        }

        // 3. Validar que no es profesor ya
        if (profesorRepository.existsByPersona(command.empleadoId())) {
            throw new DuplicateResourceException("El empleado con ID " + command.empleadoId() + " ya es profesor");
        }

        // 4. Validar grado académico según categoría
        validarGradoAcademicoSegunCategoria(command.gradoAcademico(), command.categoriaDocente());

        // 5. Crear profesor
        Profesor profesor = new Profesor();
        profesor.setEmpleado(empleado);
        profesor.setGradoAcademico(command.gradoAcademico());
        profesor.setCategoriaDocente(command.categoriaDocente());
        profesor.setDedicacion(command.dedicacion());
        profesor.setCodigoRenacyt(command.codigoRenacyt());
        profesor.setEspecialidad(command.especialidad());

        profesorRepository.persist(profesor);

        // Sincronizar roles
        roleSyncService.syncAllRolesForPersona(empleado.getPersona());

        return profesor;
    }

    private void validarGradoAcademicoSegunCategoria(String gradoAcademico, String categoriaDocente) {
        switch (categoriaDocente) {
            case "PRINCIPAL":
                if (!gradoAcademico.equals("DOCTOR") && !gradoAcademico.equals("MAGISTER")) {
                    throw new BusinessException("La categoría PRINCIPAL requiere grado académico de DOCTOR o MAGISTER");
                }
                break;
            case "ASOCIADO":
                if (gradoAcademico.equals("BACHILLER") || gradoAcademico.equals("LICENCIADO")) {
                    throw new BusinessException("La categoría ASOCIADO requiere grado académico de MAGISTER o DOCTOR");
                }
                break;
            case "AUXILIAR":
                if (gradoAcademico.equals("BACHILLER")) {
                    throw new BusinessException("La categoría AUXILIAR requiere mínimo grado académico de LICENCIADO");
                }
                break;
            default:
                throw new BusinessException("Categoría docente no válida: " + categoriaDocente);
        }
    }
}
