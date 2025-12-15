package upeu.edu.pe.core.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.core.application.dto.TipoLocalizacionRequestDTO;
import upeu.edu.pe.core.application.dto.TipoLocalizacionResponseDTO;
import upeu.edu.pe.core.domain.entities.TipoLocalizacion;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TipoLocalizacionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    TipoLocalizacion toEntity(TipoLocalizacionRequestDTO dto);

    @Mapping(target = "padreId", source = "padre.id")
    @Mapping(target = "padreNombre", source = "padre.nombre")
    @Mapping(target = "padreCodigo", source = "padre.codigo")
    TipoLocalizacionResponseDTO toResponseDTO(TipoLocalizacion entity);

    List<TipoLocalizacionResponseDTO> toResponseDTOList(List<TipoLocalizacion> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(TipoLocalizacionRequestDTO dto, @MappingTarget TipoLocalizacion entity);
}
