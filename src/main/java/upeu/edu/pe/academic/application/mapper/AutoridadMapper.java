package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.AutoridadDTO;
import upeu.edu.pe.academic.application.dto.CreateAutoridadDTO;
import upeu.edu.pe.academic.application.dto.UpdateAutoridadDTO;
import upeu.edu.pe.academic.domain.entities.Autoridad;

@Mapper(componentModel = "cdi")
public interface AutoridadMapper {

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "personaId", source = "persona.id")
    @Mapping(target = "personaNombre", expression = "java(getPersonaNombreCompleto(entity))")
    @Mapping(target = "personaFotoUrl", source = "persona.fotoUrl")
    @Mapping(target = "tipoAutoridadId", source = "tipoAutoridad.id")
    @Mapping(target = "tipoAutoridadNombre", source = "tipoAutoridad.nombre")
    @Mapping(target = "nivelJerarquia", source = "tipoAutoridad.nivelJerarquia")
    AutoridadDTO toDTO(Autoridad entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @Mapping(target = "tipoAutoridad", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Autoridad toEntity(CreateAutoridadDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @Mapping(target = "tipoAutoridad", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDTO(UpdateAutoridadDTO dto, @MappingTarget Autoridad entity);

    default String getPersonaNombreCompleto(Autoridad entity) {
        if (entity.getPersona() == null) return null;
        var persona = entity.getPersona();
        return String.format("%s %s %s", 
            persona.getNombres() != null ? persona.getNombres() : "",
            persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "",
            persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : ""
        ).trim();
    }
}
