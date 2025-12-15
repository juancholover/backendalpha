package upeu.edu.pe.people.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.core.domain.repositories.PersonaRepository;
import upeu.edu.pe.core.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.curriculum.domain.entities.ProgramaAcademico;
import upeu.edu.pe.curriculum.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.people.domain.commands.ActualizarAutoridadCommand;
import upeu.edu.pe.people.domain.entities.Autoridad;
import upeu.edu.pe.people.domain.entities.TipoAutoridad;
import upeu.edu.pe.people.domain.repositories.AutoridadRepository;
import upeu.edu.pe.people.domain.repositories.TipoAutoridadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Actualizar una autoridad.
 */
@ApplicationScoped
public class ActualizarAutoridadUseCase {

    @Inject
    AutoridadRepository autoridadRepository;

    @Inject
    PersonaRepository personaRepository;

    @Inject
    TipoAutoridadRepository tipoAutoridadRepository;

    @Inject
    UnidadOrganizativaRepository unidadOrganizativaRepository;

    @Inject
    ProgramaAcademicoRepository programaAcademicoRepository;

    @Transactional
    public Autoridad execute(ActualizarAutoridadCommand command) {
        // Buscar autoridad existente
        Autoridad autoridad = autoridadRepository.findByIdOptional(command.id())
                .orElseThrow(() -> new NotFoundException(
                        "Autoridad no encontrada con ID: " + command.id()));

        // Actualizar persona si se proporciona
        if (command.personaId() != null) {
            Persona persona = personaRepository.findByIdOptional(command.personaId())
                    .orElseThrow(() -> new NotFoundException(
                            "Persona no encontrada con ID: " + command.personaId()));
            autoridad.setPersona(persona);
        }

        // Actualizar tipo de autoridad si se proporciona
        if (command.tipoAutoridadId() != null) {
            TipoAutoridad tipoAutoridad = tipoAutoridadRepository.findByIdOptional(command.tipoAutoridadId())
                    .orElseThrow(() -> new NotFoundException(
                            "Tipo de autoridad no encontrado con ID: " + command.tipoAutoridadId()));
            autoridad.setTipoAutoridad(tipoAutoridad);
        }

        // Actualizar campos simples
        if (command.fechaInicio() != null) {
            autoridad.setFechaInicio(command.fechaInicio());
        }
        if (command.fechaFin() != null) {
            autoridad.setFechaFin(command.fechaFin());
        }
        if (command.esVigente() != null) {
            autoridad.setEsVigente(command.esVigente());
        }
        if (command.resolucionDesignacion() != null) {
            autoridad.setResolucionDesignacion(command.resolucionDesignacion());
        }
        if (command.observaciones() != null) {
            autoridad.setObservaciones(command.observaciones());
        }

        // Actualizar unidad organizativa
        if (command.unidadOrganizativaId() != null) {
            UnidadOrganizativa unidad = unidadOrganizativaRepository
                    .findByIdOptional(command.unidadOrganizativaId())
                    .orElseThrow(() -> new NotFoundException(
                            "Unidad organizativa no encontrada con ID: " + command.unidadOrganizativaId()));
            autoridad.setUnidadOrganizativa(unidad);
        }

        // Actualizar programa académico
        if (command.programaAcademicoId() != null) {
            ProgramaAcademico programa = programaAcademicoRepository
                    .findByIdOptional(command.programaAcademicoId())
                    .orElseThrow(() -> new NotFoundException(
                            "Programa académico no encontrado con ID: " + command.programaAcademicoId()));
            autoridad.setProgramaAcademico(programa);
        }

        autoridadRepository.persist(autoridad);
        return autoridad;
    }
}
