package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.PersonaRequestDTO;
import upeu.edu.pe.academic.application.dto.PersonaResponseDTO;
import upeu.edu.pe.academic.domain.entities.Persona;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonaMapper {

    /**
     * Convierte PersonaRequestDTO a Persona (para crear)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Persona toEntity(PersonaRequestDTO dto);

    /**
     * Actualiza una entidad Persona existente desde PersonaRequestDTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(PersonaRequestDTO dto, @MappingTarget Persona entity);

    /**
     * Convierte Persona a PersonaResponseDTO
     */
    @Mapping(target = "nombreCompleto", expression = "java(buildNombreCompleto(entity))")
    @Mapping(target = "edad", expression = "java(calculateEdad(entity.getFechaNacimiento()))")
    PersonaResponseDTO toResponseDTO(Persona entity);

    /**
     * Construye el nombre completo concatenando nombres y apellidos
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

    /**
     * Calcula la edad basándose en la fecha de nacimiento
     */
    default Integer calculateEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}
