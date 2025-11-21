package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.UniversidadRequestDTO;
import upeu.edu.pe.academic.application.dto.UniversidadResponseDTO;
import upeu.edu.pe.academic.domain.entities.Universidad;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UniversidadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "localizacionPrincipal.id", source = "localizacionPrincipalId")
    Universidad toEntity(UniversidadRequestDTO dto);

    @Mapping(target = "localizacionPrincipalId", source = "localizacionPrincipal.id")
    @Mapping(target = "localizacionPrincipalNombre", source = "localizacionPrincipal.nombre")
    UniversidadResponseDTO toResponseDTO(Universidad entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "localizacionPrincipal.id", source = "localizacionPrincipalId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UniversidadRequestDTO dto, @MappingTarget Universidad entity);
}
