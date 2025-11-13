package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.UniversidadRequestDTO;
import upeu.edu.pe.academic.application.dto.UniversidadResponseDTO;
import upeu.edu.pe.academic.domain.entities.Universidad;

@Mapper(componentModel = "cdi")
public interface UniversidadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Universidad toEntity(UniversidadRequestDTO dto);

    UniversidadResponseDTO toResponseDTO(Universidad entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(UniversidadRequestDTO dto, @MappingTarget Universidad entity);
}
