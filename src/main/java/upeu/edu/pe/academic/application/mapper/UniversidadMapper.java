package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.UniversidadRequestDTO;
import upeu.edu.pe.academic.application.dto.UniversidadResponseDTO;
import upeu.edu.pe.academic.domain.entities.Universidad;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UniversidadMapper {

    @Mapping(target = "id", ignore = true)
    Universidad toEntity(UniversidadRequestDTO dto);

    @Mapping(target = "localizacionPrincipalId", ignore = true)
    @Mapping(target = "localizacionPrincipalNombre", ignore = true)
    UniversidadResponseDTO toResponseDTO(Universidad entity);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UniversidadRequestDTO dto, @MappingTarget Universidad entity);
}
