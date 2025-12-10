package upeu.edu.pe.people.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.people.application.dto.CreateTipoAutoridadDTO;
import upeu.edu.pe.people.application.dto.TipoAutoridadDTO;
import upeu.edu.pe.people.application.dto.UpdateTipoAutoridadDTO;
import upeu.edu.pe.people.domain.entities.TipoAutoridad;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TipoAutoridadMapper {

    TipoAutoridadDTO toDTO(TipoAutoridad entity);

    @Mapping(target = "id", ignore = true)
    TipoAutoridad toEntity(CreateTipoAutoridadDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(UpdateTipoAutoridadDTO dto, @MappingTarget TipoAutoridad entity);
}

