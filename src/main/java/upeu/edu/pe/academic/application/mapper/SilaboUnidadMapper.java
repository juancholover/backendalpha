package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.SilaboUnidadRequestDTO;
import upeu.edu.pe.academic.application.dto.SilaboUnidadResponseDTO;
import upeu.edu.pe.academic.domain.entities.SilaboUnidad;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {SilaboActividadMapper.class})
public interface SilaboUnidadMapper {

    /**
     * Convierte SilaboUnidadRequestDTO a SilaboUnidad (para crear)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "silabo", ignore = true)
    @Mapping(target = "actividades", ignore = true)
    SilaboUnidad toEntity(SilaboUnidadRequestDTO dto);

    /**
     * Actualiza una entidad SilaboUnidad existente desde DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "silabo", ignore = true)
    @Mapping(target = "actividades", ignore = true)
    void updateEntityFromDto(SilaboUnidadRequestDTO dto, @MappingTarget SilaboUnidad entity);

    /**
     * Convierte SilaboUnidad a SilaboUnidadResponseDTO
     */
    @Mapping(target = "silaboId", source = "silabo.id")
    @Mapping(target = "duracionSemanas", expression = "java(entity.getDuracionSemanas())")
    @Mapping(target = "cantidadActividades", expression = "java(entity.getActividades() != null ? entity.getActividades().size() : 0)")
    @Mapping(target = "actividades", source = "actividades")
    SilaboUnidadResponseDTO toResponseDTO(SilaboUnidad entity);

    /**
     * Convierte SilaboUnidad a SilaboUnidadResponseDTO sin actividades (para listados)
     */
    @Mapping(target = "silaboId", source = "silabo.id")
    @Mapping(target = "duracionSemanas", expression = "java(entity.getDuracionSemanas())")
    @Mapping(target = "cantidadActividades", expression = "java(entity.getActividades() != null ? entity.getActividades().size() : 0)")
    @Mapping(target = "actividades", ignore = true)
    SilaboUnidadResponseDTO toResponseDTOWithoutActividades(SilaboUnidad entity);
}
