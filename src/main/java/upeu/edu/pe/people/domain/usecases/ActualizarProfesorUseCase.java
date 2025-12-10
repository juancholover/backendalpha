package upeu.edu.pe.people.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.people.domain.commands.ActualizarProfesorCommand;
import upeu.edu.pe.people.domain.entities.Profesor;
import upeu.edu.pe.people.domain.repositories.ProfesorRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Actualizar un profesor.
 */
@ApplicationScoped
public class ActualizarProfesorUseCase {

    @Inject
    ProfesorRepository profesorRepository;

    @Transactional
    public Profesor execute(ActualizarProfesorCommand command) {

        // 1. Buscar profesor
        Profesor profesor = profesorRepository.findByIdOptional(command.id())
                .orElseThrow(() -> new NotFoundException("Profesor no encontrado con ID: " + command.id()));

        // 2. Validar grado según categoría
        validarGradoAcademicoSegunCategoria(command.gradoAcademico(), command.categoriaDocente());

        // 3. Actualizar
        profesor.setGradoAcademico(command.gradoAcademico());
        profesor.setCategoriaDocente(command.categoriaDocente());
        profesor.setDedicacion(command.dedicacion());
        profesor.setCodigoRenacyt(command.codigoRenacyt());
        profesor.setEspecialidad(command.especialidad());

        profesorRepository.persist(profesor);

        return profesor;
    }

    private void validarGradoAcademicoSegunCategoria(String gradoAcademico, String categoriaDocente) {
        switch (categoriaDocente) {
            case "PRINCIPAL":
                if (!gradoAcademico.equals("DOCTOR") && !gradoAcademico.equals("MAGISTER")) {
                    throw new BusinessException("La categoría PRINCIPAL requiere grado DOCTOR o MAGISTER");
                }
                break;
            case "ASOCIADO":
                if (gradoAcademico.equals("BACHILLER") || gradoAcademico.equals("LICENCIADO")) {
                    throw new BusinessException("La categoría ASOCIADO requiere grado MAGISTER o DOCTOR");
                }
                break;
            case "AUXILIAR":
                if (gradoAcademico.equals("BACHILLER")) {
                    throw new BusinessException("La categoría AUXILIAR requiere mínimo LICENCIADO");
                }
                break;
            default:
                throw new BusinessException("Categoría docente no válida: " + categoriaDocente);
        }
    }
}
