package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.EstudianteRequestDTO;
import upeu.edu.pe.academic.application.dto.EstudianteResponseDTO;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.Persona;


@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EstudianteMapper {

    /**
     * Convierte EstudianteRequestDTO a Estudiante (para crear)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "persona", ignore = true) // Se asigna manualmente en el service
    @Mapping(target = "programaAcademico", ignore = true) // Se asigna manualmente en el service
    Estudiante toEntity(EstudianteRequestDTO dto);

    /**
     * Actualiza una entidad Estudiante existente desde EstudianteRequestDTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @Mapping(target = "programaAcademico", ignore = true)
    void updateEntityFromDto(EstudianteRequestDTO dto, @MappingTarget Estudiante entity);

    /**
     * Convierte Estudiante a EstudianteResponseDTO
     */
    @Mapping(target = "personaId", source = "persona.id")
    @Mapping(target = "nombreCompleto", expression = "java(buildNombreCompleto(entity.getPersona()))")
    @Mapping(target = "numeroDocumento", source = "persona.numeroDocumento")
    @Mapping(target = "email", source = "persona.email")
    @Mapping(target = "programaAcademicoId", source = "programaAcademico.id")
    @Mapping(target = "programaNombre", source = "programaAcademico.nombre")
    @Mapping(target = "programaCodigo", source = "programaAcademico.codigo")
    EstudianteResponseDTO toResponseDTO(Estudiante entity);

    /**
     * Construye el nombre completo de la persona
     */
    default String buildNombreCompleto(Persona persona) {
        if (persona == null) {
            return null;
        }
        return String.format("%s %s %s", 
            persona.getNombres() != null ? persona.getNombres() : "",
            persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "",
            persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : ""
        ).trim();
    }
}
