package upeu.edu.pe.security.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.security.application.dto.RolPermisoRequestDTO;
import upeu.edu.pe.security.application.dto.RolPermisoResponseDTO;
import upeu.edu.pe.security.domain.entities.RolPermiso;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RolPermisoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "permiso", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    RolPermiso toEntity(RolPermisoRequestDTO dto);

    @Mapping(target = "rolId", source = "rol.id")
    @Mapping(target = "rolNombre", source = "rol.nombre")
    @Mapping(target = "permisoId", source = "permiso.id")
    @Mapping(target = "permisoNombreClave", source = "permiso.nombreClave")
    @Mapping(target = "permisoModulo", source = "permiso.modulo")
    @Mapping(target = "permisoRecurso", source = "permiso.recurso")
    @Mapping(target = "permisoAccion", source = "permiso.accion")
    RolPermisoResponseDTO toResponseDTO(RolPermiso entity);

    List<RolPermisoResponseDTO> toResponseDTOList(List<RolPermiso> entities);
}
