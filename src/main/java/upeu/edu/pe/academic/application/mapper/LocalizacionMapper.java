package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.LocalizacionRequestDTO;
import upeu.edu.pe.academic.application.dto.LocalizacionResponseDTO;
import upeu.edu.pe.academic.domain.entities.Localizacion;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocalizacionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoLocalizacion.id", source = "tipoLocalizacionId")
    @Mapping(target = "active", constant = "true")
    Localizacion toEntity(LocalizacionRequestDTO dto);

    @Mapping(target = "tipoLocalizacionId", source = "tipoLocalizacion.id")
    @Mapping(target = "tipoLocalizacionNombre", source = "tipoLocalizacion.nombre")
    LocalizacionResponseDTO toResponseDTO(Localizacion entity);

    List<LocalizacionResponseDTO> toResponseDTOList(List<Localizacion> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoLocalizacion.id", source = "tipoLocalizacionId")
    void updateEntityFromDTO(LocalizacionRequestDTO dto, @MappingTarget Localizacion entity);
}
