package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.UnidadOrganizativaRequestDTO;
import upeu.edu.pe.academic.application.dto.UnidadOrganizativaResponseDTO;
import upeu.edu.pe.academic.domain.entities.UnidadOrganizativa;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UnidadOrganizativaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoUnidad.id", source = "tipoDeUnidadId")
    @Mapping(target = "unidadPadre.id", source = "unidadPadreId")
    @Mapping(target = "localizacion.id", source = "localizacionId")
    @Mapping(target = "active", constant = "true")
    UnidadOrganizativa toEntity(UnidadOrganizativaRequestDTO dto);

    @Mapping(target = "tipoDeUnidadId", source = "tipoUnidad.id")
    @Mapping(target = "tipoDeUnidadNombre", source = "tipoUnidad.nombre")
    @Mapping(target = "unidadPadreId", source = "unidadPadre.id")
    @Mapping(target = "unidadPadreNombre", source = "unidadPadre.nombre")
    @Mapping(target = "localizacionId", source = "localizacion.id")
    @Mapping(target = "localizacionNombre", source = "localizacion.nombre")
    UnidadOrganizativaResponseDTO toResponseDTO(UnidadOrganizativa entity);

    List<UnidadOrganizativaResponseDTO> toResponseDTOList(List<UnidadOrganizativa> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipoUnidad.id", source = "tipoDeUnidadId")
    @Mapping(target = "unidadPadre.id", source = "unidadPadreId")
    @Mapping(target = "localizacion.id", source = "localizacionId")
    void updateEntityFromDTO(UnidadOrganizativaRequestDTO dto, @MappingTarget UnidadOrganizativa entity);
}
