package upeu.edu.pe.security.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import upeu.edu.pe.security.application.dto.AuthUsuarioPermisoResponseDTO;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.entities.AuthUsuarioPermiso;
import upeu.edu.pe.core.domain.entities.Persona;

@Mapper(componentModel = "cdi")
public interface AuthUsuarioPermisoMapper {

    @Mapping(target = "authUsuarioId", source = "authUsuario.id")
    @Mapping(target = "nombreUsuario", source = "authUsuario", qualifiedByName = "buildNombreCompleto")
    @Mapping(target = "permisoId", source = "permiso.id")
    @Mapping(target = "codigoPermiso", source = "permiso.nombreClave")
    @Mapping(target = "nombrePermiso", source = "permiso.descripcion")
    @Mapping(target = "asignadoPorId", source = "asignadoPor.id")
    @Mapping(target = "asignadoPorNombre", source = "asignadoPor", qualifiedByName = "buildNombreCompleto")
    @Mapping(target = "estaVigente", expression = "java(entity.estaVigente())")
    @Mapping(target = "estaExpirado", expression = "java(entity.estaExpirado())")
    @Mapping(target = "creadoEn", source = "createdAt")
    @Mapping(target = "creadoPor", source = "createdBy")
    AuthUsuarioPermisoResponseDTO toDto(AuthUsuarioPermiso entity);

    @Named("buildNombreCompleto")
    default String buildNombreCompleto(AuthUsuario authUsuario) {
        if (authUsuario == null || authUsuario.getPersona() == null) {
            return null;
        }
        Persona p = authUsuario.getPersona();
        return (p.getNombres() + " " + p.getApellidoPaterno() + " " + 
                (p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "")).trim();
    }
}
