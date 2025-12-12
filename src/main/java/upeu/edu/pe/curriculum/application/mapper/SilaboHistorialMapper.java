package upeu.edu.pe.curriculum.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import upeu.edu.pe.curriculum.application.dto.SilaboHistorialDTO;
import upeu.edu.pe.curriculum.domain.entities.SilaboHistorial;

/**
 * Mapper para convertir entre SilaboHistorial y SilaboHistorialDTO
 */
@Mapper(componentModel = "cdi")
public interface SilaboHistorialMapper {

    @Mapping(source = "silabo.id", target = "silaboId")
    SilaboHistorialDTO toDTO(SilaboHistorial entity);
}
