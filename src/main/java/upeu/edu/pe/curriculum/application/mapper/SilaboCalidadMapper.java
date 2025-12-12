package upeu.edu.pe.curriculum.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import upeu.edu.pe.curriculum.application.dto.SilaboCalidadDTO;
import upeu.edu.pe.curriculum.domain.entities.SilaboCalidad;

/**
 * Mapper para convertir entre SilaboCalidad y SilaboCalidadDTO
 */
@Mapper(componentModel = "cdi")
public interface SilaboCalidadMapper {

    @Mapping(source = "silabo.id", target = "silaboId")
    SilaboCalidadDTO toDTO(SilaboCalidad entity);

    @Mapping(target = "silabo", ignore = true) // Se maneja en el servicio
    @Mapping(target = "active", constant = "true") // Por defecto activo
    @Mapping(target = "detallesEvaluacion", ignore = true) // Campo JSON, no incluido en DTO simple
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    SilaboCalidad toEntity(SilaboCalidadDTO dto);
}
