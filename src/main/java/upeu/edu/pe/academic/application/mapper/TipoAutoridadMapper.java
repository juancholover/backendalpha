package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.CreateTipoAutoridadDTO;
import upeu.edu.pe.academic.application.dto.TipoAutoridadDTO;
import upeu.edu.pe.academic.application.dto.UpdateTipoAutoridadDTO;
import upeu.edu.pe.academic.domain.entities.TipoAutoridad;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TipoAutoridadMapper {

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "universidadNombre", source = "universidad.nombre")
    TipoAutoridadDTO toDTO(TipoAutoridad entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad.id", source = "universidadId")
    TipoAutoridad toEntity(CreateTipoAutoridadDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true) // No actualizar la universidad
    void updateEntityFromDTO(UpdateTipoAutoridadDTO dto, @MappingTarget TipoAutoridad entity);
}
