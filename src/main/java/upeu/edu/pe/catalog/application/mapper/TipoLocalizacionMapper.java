package upeu.edu.pe.catalog.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.catalog.application.dto.TipoLocalizacionRequestDTO;
import upeu.edu.pe.catalog.application.dto.TipoLocalizacionResponseDTO;
import upeu.edu.pe.academic.domain.entities.TipoLocalizacion;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TipoLocalizacionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    TipoLocalizacion toEntity(TipoLocalizacionRequestDTO dto);

    TipoLocalizacionResponseDTO toResponseDTO(TipoLocalizacion entity);

    List<TipoLocalizacionResponseDTO> toResponseDTOList(List<TipoLocalizacion> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(TipoLocalizacionRequestDTO dto, @MappingTarget TipoLocalizacion entity);
}
