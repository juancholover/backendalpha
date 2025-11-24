package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.UnidadOrganizativaRequestDTO;
import upeu.edu.pe.academic.application.dto.UnidadOrganizativaResponseDTO;
import upeu.edu.pe.academic.domain.entities.UnidadOrganizativa;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UnidadOrganizativaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad.id", source = "universidadId")
    @Mapping(target = "localizacion.id", source = "localizacionId")
    @Mapping(target = "tipoUnidad.id", source = "tipoUnidadId")
    @Mapping(target = "unidadPadre.id", source = "unidadPadreId")
    @Mapping(target = "active", constant = "true")
    UnidadOrganizativa toEntity(UnidadOrganizativaRequestDTO dto);

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "universidadNombre", source = "universidad.nombre")
    @Mapping(target = "localizacionId", source = "localizacion.id")
    @Mapping(target = "localizacionNombre", source = "localizacion.nombre")
    @Mapping(target = "tipoUnidadId", source = "tipoUnidad.id")
    @Mapping(target = "tipoUnidadNombre", source = "tipoUnidad.nombre")
    @Mapping(target = "tipoUnidadNivel", source = "tipoUnidad.nivel")
    @Mapping(target = "unidadPadreId", source = "unidadPadre.id")
    @Mapping(target = "unidadPadreNombre", source = "unidadPadre.nombre")
    UnidadOrganizativaResponseDTO toResponseDTO(UnidadOrganizativa entity);

    List<UnidadOrganizativaResponseDTO> toResponseDTOList(List<UnidadOrganizativa> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad.id", source = "universidadId")
    @Mapping(target = "localizacion.id", source = "localizacionId")
    @Mapping(target = "tipoUnidad.id", source = "tipoUnidadId")
    @Mapping(target = "unidadPadre.id", source = "unidadPadreId")
    void updateEntityFromDTO(UnidadOrganizativaRequestDTO dto, @MappingTarget UnidadOrganizativa entity);
}
