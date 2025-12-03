package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.SilaboActividadRequestDTO;
import upeu.edu.pe.academic.application.dto.SilaboActividadResponseDTO;
import upeu.edu.pe.academic.domain.entities.SilaboActividad;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SilaboActividadMapper {

    /**
     * Convierte SilaboActividadRequestDTO a SilaboActividad (para crear)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    SilaboActividad toEntity(SilaboActividadRequestDTO dto);

    /**
     * Actualiza una entidad SilaboActividad existente desde DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "unidad", ignore = true)
    void updateEntityFromDto(SilaboActividadRequestDTO dto, @MappingTarget SilaboActividad entity);

    /**
     * Convierte SilaboActividad a SilaboActividadResponseDTO
     */
    @Mapping(target = "unidadId", source = "unidad.id")
    @Mapping(target = "esFormativa", expression = "java(entity.esFormativa())")
    @Mapping(target = "esSumativa", expression = "java(entity.esSumativa())")
    SilaboActividadResponseDTO toResponseDTO(SilaboActividad entity);
}
