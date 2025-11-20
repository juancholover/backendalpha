package upeu.edu.pe.security.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import upeu.edu.pe.security.application.dto.PasswordChangeDto;
import upeu.edu.pe.security.application.dto.UserRequestDto;
import upeu.edu.pe.security.application.dto.UserResponseDto;
import upeu.edu.pe.security.application.dto.UserUpdateDto;
import upeu.edu.pe.security.application.mapper.UserMapper;
import upeu.edu.pe.security.domain.entities.User;
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;
import upeu.edu.pe.security.domain.repositories.UserRepository;
import upeu.edu.pe.security.infrastructure.utils.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    UserMapper userMapper;

    @Inject
    PasswordEncoder passwordEncoder;

    public List<UserResponseDto> findAll() {
        return userRepository.findAllActive()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> findAllByStatus(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        return userRepository.findAllByStatus(status)
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> findAllByRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        return userRepository.findAllByRole(role)
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto findById(Long id) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));
        
        if (!user.getActive()) {
            throw new NotFoundException("Usuario eliminado con ID: " + id);
        }
        
        return userMapper.toResponseDto(user);
    }

    public UserResponseDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con username: " + username));
        
        return userMapper.toResponseDto(user);
    }

    @Transactional
    public UserResponseDto create(UserRequestDto requestDto) {
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario con el username: " + requestDto.getUsername());
        }
        
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + requestDto.getEmail());
        }

        User user = userMapper.toEntity(requestDto);
        
        // Hashear contraseña con PasswordEncoder
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        userRepository.persist(user);
        return userMapper.toResponseDto(user);
    }

    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto updateDto) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        if (!user.getActive()) {
            throw new IllegalStateException("No se puede actualizar un usuario eliminado");
        }

        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDto.getEmail())) {
                throw new IllegalArgumentException("Ya existe otro usuario con el email: " + updateDto.getEmail());
            }
        }

        userMapper.updateEntityFromDto(updateDto, user);
        userRepository.persist(user);
        return userMapper.toResponseDto(user);
    }

    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        user.setActive(false);
        userRepository.persist(user);
    }

    @Transactional
    public void changePassword(Long id, PasswordChangeDto passwordChangeDto) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        if (!user.getActive()) {
            throw new IllegalStateException("No se puede cambiar la contraseña de un usuario eliminado");
        }

        // Validar contraseña actual con PasswordEncoder
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new NotAuthorizedException("La contraseña actual es incorrecta");
        }

        // Hash de nueva contraseña con PasswordEncoder
        String hashedPassword = passwordEncoder.encode(passwordChangeDto.getNewPassword());
        
        user.setPassword(hashedPassword);
        userRepository.persist(user);
    }

    @Transactional
    public void updateLastLogin(Long id) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        user.setLastLogin(LocalDateTime.now());
        userRepository.persist(user);
    }

    public long countByRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        return userRepository.countByRole(role);
    }

    public long countByStatus(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        return userRepository.countByStatus(status);
    }
}