package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.CreateTipoAutoridadDTO;
import upeu.edu.pe.academic.application.dto.TipoAutoridadDTO;
import upeu.edu.pe.academic.application.dto.UpdateTipoAutoridadDTO;
import upeu.edu.pe.academic.domain.entities.TipoAutoridad;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TipoAutoridadMapper {

    TipoAutoridadDTO toDTO(TipoAutoridad entity);

    @Mapping(target = "id", ignore = true)
    TipoAutoridad toEntity(CreateTipoAutoridadDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(UpdateTipoAutoridadDTO dto, @MappingTarget TipoAutoridad entity);
}
