package upeu.edu.pe.catalog.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.catalog.application.dto.TipoUnidadRequestDTO;
import upeu.edu.pe.catalog.application.dto.TipoUnidadResponseDTO;
import upeu.edu.pe.academic.domain.entities.TipoUnidad;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TipoUnidadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    TipoUnidad toEntity(TipoUnidadRequestDTO dto);

    TipoUnidadResponseDTO toResponseDTO(TipoUnidad entity);

    List<TipoUnidadResponseDTO> toResponseDTOList(List<TipoUnidad> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(TipoUnidadRequestDTO dto, @MappingTarget TipoUnidad entity);
}
