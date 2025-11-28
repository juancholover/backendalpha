package upeu.edu.pe.security.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.security.application.dto.UserResponseDto;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface UserMapper {

    @Mapping(target = "username", source = "persona.email")
    @Mapping(target = "email", source = "persona.email")
    @Mapping(target = "firstName", source = "persona.nombres")
    @Mapping(target = "lastName", source = "persona.apellidoPaterno")
    @Mapping(target = "phone", source = "persona.telefono")
    @Mapping(target = "lastLogin", source = "ultimoAcceso")
    @Mapping(target = "role", expression = "java(mapRole(authUsuario.getRol().getNombre()))")
    @Mapping(target = "status", expression = "java(mapStatus(authUsuario))")
    UserResponseDto toResponseDto(AuthUsuario authUsuario);

    List<UserResponseDto> toResponseDtoList(List<AuthUsuario> authUsuarios);

    default UserRole mapRole(String rolNombre) {
        if (rolNombre == null)
            return UserRole.USER;
        try {
            return UserRole.valueOf(rolNombre.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UserRole.USER;
        }
    }

    default UserStatus mapStatus(AuthUsuario authUsuario) {
        if (authUsuario.estaBloqueado()) {
            return UserStatus.SUSPENDED;
        }
        if (!authUsuario.estaActivo()) {
            return UserStatus.INACTIVE;
        }
        return UserStatus.ACTIVE;
    }
}