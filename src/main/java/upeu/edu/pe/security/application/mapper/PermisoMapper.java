package upeu.edu.pe.security.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.security.application.dto.PermisoRequestDTO;
import upeu.edu.pe.security.application.dto.PermisoResponseDTO;
import upeu.edu.pe.security.domain.entities.Permiso;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PermisoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombreClave", ignore = true)
    @Mapping(target = "rolPermisos", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    Permiso toEntity(PermisoRequestDTO dto);

    @Mapping(target = "cantidadRoles", ignore = true)
    PermisoResponseDTO toResponseDTO(Permiso entity);

    List<PermisoResponseDTO> toResponseDTOList(List<Permiso> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombreClave", ignore = true)
    @Mapping(target = "rolPermisos", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(PermisoRequestDTO dto, @MappingTarget Permiso entity);
}
