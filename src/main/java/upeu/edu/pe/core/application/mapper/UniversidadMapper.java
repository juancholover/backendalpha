package upeu.edu.pe.core.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.core.application.dto.UniversidadRequestDTO;
import upeu.edu.pe.core.application.dto.UniversidadResponseDTO;
import upeu.edu.pe.core.domain.entities.Universidad;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UniversidadMapper {

    @Mapping(target = "id", ignore = true)
    Universidad toEntity(UniversidadRequestDTO dto);

    UniversidadResponseDTO toResponseDTO(Universidad entity);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UniversidadRequestDTO dto, @MappingTarget Universidad entity);
}

