package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.core.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.curriculum.domain.commands.ActualizarProgramaAcademicoCommand;
import upeu.edu.pe.curriculum.domain.entities.ProgramaAcademico;
import upeu.edu.pe.curriculum.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Actualizar programa académico.
 */
@ApplicationScoped
public class ActualizarProgramaAcademicoUseCase {

    @Inject
    ProgramaAcademicoRepository programaRepository;

    @Inject
    UnidadOrganizativaRepository unidadRepository;

    @Transactional
    public ProgramaAcademico execute(ActualizarProgramaAcademicoCommand command) {

        // 1. Obtener programa existente
        ProgramaAcademico programa = programaRepository.findByIdOptional(command.id())
                .orElseThrow(() -> new NotFoundException("Programa académico no encontrado con ID: " + command.id()));

        // 2. Validar código único si cambió
        if (!programa.getCodigo().equals(command.codigo()) &&
                programaRepository.existsByCodigoAndIdNot(command.codigo(), command.id())) {
            throw new DuplicateResourceException("ProgramaAcademico", "codigo", command.codigo());
        }

        // 3. Validar unidad organizativa si cambió
        if (!programa.getUnidadOrganizativa().getId().equals(command.unidadOrganizativaId())) {
            UnidadOrganizativa unidad = unidadRepository.findByIdOptional(command.unidadOrganizativaId())
                    .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada"));
            programa.setUnidadOrganizativa(unidad);
        }

        // 4. Actualizar
        programa.setCodigo(command.codigo());
        programa.setNombre(command.nombre());
        programa.setGradoAcademico(command.grado());
        programa.setNivelAcademico(command.nivelAcademico());
        programa.setModalidad(command.modalidad());
        programa.setDuracionSemestres(command.duracionCiclos());
        if (command.estado() != null) {
            programa.setEstado(command.estado());
        }

        programaRepository.persist(programa);

        return programa;
    }
}
