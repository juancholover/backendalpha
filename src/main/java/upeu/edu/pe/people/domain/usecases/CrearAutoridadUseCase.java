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
import upeu.edu.pe.people.domain.commands.CrearAutoridadCommand;
import upeu.edu.pe.people.domain.entities.Autoridad;
import upeu.edu.pe.people.domain.entities.TipoAutoridad;
import upeu.edu.pe.people.domain.repositories.AutoridadRepository;
import upeu.edu.pe.people.domain.repositories.TipoAutoridadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Crear una autoridad.
 */
@ApplicationScoped
public class CrearAutoridadUseCase {

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
    public Autoridad execute(CrearAutoridadCommand command) {
        // Validar persona
        Persona persona = personaRepository.findByIdOptional(command.personaId())
                .orElseThrow(() -> new NotFoundException(
                        "Persona no encontrada con ID: " + command.personaId()));

        // Validar tipo de autoridad
        TipoAutoridad tipoAutoridad = tipoAutoridadRepository.findByIdOptional(command.tipoAutoridadId())
                .orElseThrow(() -> new NotFoundException(
                        "Tipo de autoridad no encontrado con ID: " + command.tipoAutoridadId()));

        // Crear entidad
        Autoridad autoridad = new Autoridad();
        autoridad.setPersona(persona);
        autoridad.setTipoAutoridad(tipoAutoridad);
        autoridad.setFechaInicio(command.fechaInicio());
        autoridad.setFechaFin(command.fechaFin());
        autoridad.setResolucionDesignacion(command.resolucionDesignacion());
        autoridad.setObservaciones(command.observaciones());
        autoridad.setEsVigente(true); // Por defecto es vigente al crear

        // Asignar unidad organizativa si se proporciona
        if (command.unidadOrganizativaId() != null) {
            UnidadOrganizativa unidad = unidadOrganizativaRepository
                    .findByIdOptional(command.unidadOrganizativaId())
                    .orElseThrow(() -> new NotFoundException(
                            "Unidad organizativa no encontrada con ID: " + command.unidadOrganizativaId()));
            autoridad.setUnidadOrganizativa(unidad);
        }

        // Asignar programa académico si se proporciona
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
