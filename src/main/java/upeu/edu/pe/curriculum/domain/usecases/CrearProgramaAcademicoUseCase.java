package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.core.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.curriculum.domain.commands.CrearProgramaAcademicoCommand;
import upeu.edu.pe.curriculum.domain.entities.ProgramaAcademico;
import upeu.edu.pe.curriculum.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Crear un nuevo programa académico.
 * 
 * Reglas de negocio:
 * - Código debe ser único
 * - Unidad organizativa debe existir
 */
@ApplicationScoped
public class CrearProgramaAcademicoUseCase {

    @Inject
    ProgramaAcademicoRepository programaRepository;

    @Inject
    UnidadOrganizativaRepository unidadRepository;

    @Transactional
    public ProgramaAcademico execute(CrearProgramaAcademicoCommand command) {

        // 1. Validar código único
        if (programaRepository.existsByCodigo(command.codigo())) {
            throw new DuplicateResourceException("ProgramaAcademico", "codigo", command.codigo());
        }

        // 2. Obtener unidad organizativa
        UnidadOrganizativa unidad = unidadRepository.findByIdOptional(command.unidadOrganizativaId())
                .orElseThrow(() -> new NotFoundException(
                        "Unidad organizativa no encontrada con ID: " + command.unidadOrganizativaId()));

        // 3. Crear programa
        ProgramaAcademico programa = new ProgramaAcademico();
        programa.setUnidadOrganizativa(unidad);
        programa.setCodigo(command.codigo());
        programa.setNombre(command.nombre());
        programa.setGradoAcademico(command.grado());
        programa.setNivelAcademico(command.nivelAcademico());
        programa.setModalidad(command.modalidad());
        programa.setDuracionSemestres(command.duracionCiclos());
        programa.setEstado("ACTIVO");

        // 4. Persistir
        programaRepository.persist(programa);

        return programa;
    }
}
