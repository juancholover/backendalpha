package upeu.edu.pe.security.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.application.dto.PasswordChangeDto;
import upeu.edu.pe.security.application.dto.UserRequestDto;
import upeu.edu.pe.security.application.dto.UserResponseDto;
import upeu.edu.pe.security.application.dto.UserUpdateDto;
import upeu.edu.pe.security.application.mapper.UserMapper;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.usecases.*;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UsuarioApplicationService {

    @Inject
    CrearUsuarioUseCase crearUseCase;

    @Inject
    ActualizarUsuarioUseCase actualizarUseCase;

    @Inject
    BuscarUsuarioUseCase buscarUseCase;

    @Inject
    EliminarUsuarioUseCase eliminarUseCase;

    @Inject
    CambiarPasswordUseCase cambiarPasswordUseCase;

    @Inject
    UserMapper userMapper;

    @Transactional
    public UserResponseDto create(UserRequestDto requestDto) {
        AuthUsuario authUsuario = crearUseCase.execute(
                requestDto.getUsername(), // Usamos username como email por ahora si es necesario, o lo separamos
                requestDto.getPassword(),
                requestDto.getFirstName(),
                requestDto.getLastName(),
                requestDto.getEmail(),
                requestDto.getPhone(),
                1L, // Rol por defecto o buscar ID de rol USER/ADMIN
                1L // Universidad por defecto o buscar ID
        );
        // Nota: El UseCase actual toma rolId y universidadId.
        // Necesitamos ajustar el DTO o la lógica para obtener estos IDs.
        // Por ahora asumiremos IDs fijos o los obtendremos de alguna manera.
        // Idealmente el DTO debería traer rolId si es un admin quien crea.

        return userMapper.toResponseDto(authUsuario);
    }

    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto updateDto) {
        AuthUsuario authUsuario = actualizarUseCase.execute(
                id,
                updateDto.getFirstName(),
                updateDto.getLastName(),
                updateDto.getEmail(),
                updateDto.getPhone(),
                null, // Rol no se actualiza por aquí por ahora
                null // Universidad no se actualiza por aquí por ahora
        );
        return userMapper.toResponseDto(authUsuario);
    }

    @Transactional
    public void delete(Long id) {
        eliminarUseCase.execute(id);
    }

    public List<UserResponseDto> findAll() {
        return buscarUseCase.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto findById(Long id) {
        return userMapper.toResponseDto(buscarUseCase.findById(id));
    }

    public UserResponseDto findByUsername(String username) {
        return userMapper.toResponseDto(buscarUseCase.findByUsername(username));
    }

    @Transactional
    public void changePassword(Long id, PasswordChangeDto passwordChangeDto) {
        cambiarPasswordUseCase.execute(id, passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    public long countByRol(Long rolId) {
        return buscarUseCase.countByRol(rolId);
    }
}
