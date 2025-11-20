package upeu.edu.pe.security.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import upeu.edu.pe.security.application.dto.*;
import upeu.edu.pe.security.application.mapper.UserMapper;
import upeu.edu.pe.security.domain.entities.RefreshToken;
import upeu.edu.pe.security.domain.entities.User;
import upeu.edu.pe.security.domain.enums.UserStatus;
import upeu.edu.pe.security.domain.repositories.RefreshTokenRepository;
import upeu.edu.pe.security.domain.repositories.UserRepository;
import upeu.edu.pe.security.infrastructure.utils.JwtTokenGenerator;
import upeu.edu.pe.security.infrastructure.utils.JwtTokenValidator;
import upeu.edu.pe.security.infrastructure.utils.PasswordEncoder;

import java.time.LocalDateTime;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;

    @Inject
    UserMapper userMapper;

    @Inject
    JwtTokenGenerator jwtTokenGenerator;

    @Inject
    JwtTokenValidator jwtTokenValidator;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    RefreshTokenRepository refreshTokenRepository;

    @Inject
    UserService userService;

    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new NotAuthorizedException("Credenciales inválidas"));

        // Validar contraseña con PasswordEncoder
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new NotAuthorizedException("Credenciales inválidas");
        }

        if (!user.getActive() || user.getStatus() != UserStatus.ACTIVE) {
            throw new NotAuthorizedException("Usuario inactivo o bloqueado");
        }

        // Generar tokens JWT
        String accessToken = jwtTokenGenerator.generateAccessToken(user);
        String refreshTokenStr = jwtTokenGenerator.generateRefreshToken(user);

        // Guardar refresh token en base de datos
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(jwtTokenGenerator.getRefreshDuration()));
        refreshToken.setIsRevoked(false);
        refreshTokenRepository.saveRefreshToken(refreshToken);

        // Actualizar último login
        userService.updateLastLogin(user.getId());

        // Construir respuesta
        AuthResponseDto.UserInfoDto userInfo = new AuthResponseDto.UserInfoDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole(),
            user.getStatus(),
            user.getLastLogin()
        );

        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenStr);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtTokenGenerator.getDuration());
        response.setUser(userInfo);

        return response;
    }

    @Transactional
    public AuthResponseDto register(RegisterRequestDto registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("El username ya está en uso");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Hashear contraseña con PasswordEncoder
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhone(registerRequest.getPhone());
        user.setRole(registerRequest.getRole());
        user.setStatus(UserStatus.ACTIVE);
        user.setActive(true);

        userRepository.persist(user);

        // Generar tokens JWT
        String accessToken = jwtTokenGenerator.generateAccessToken(user);
        String refreshTokenStr = jwtTokenGenerator.generateRefreshToken(user);

        // Guardar refresh token en base de datos
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(jwtTokenGenerator.getRefreshDuration()));
        refreshToken.setIsRevoked(false);
        refreshTokenRepository.saveRefreshToken(refreshToken);

        // Construir respuesta
        AuthResponseDto.UserInfoDto userInfo = new AuthResponseDto.UserInfoDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole(),
            user.getStatus(),
            user.getLastLogin()
        );

        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenStr);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtTokenGenerator.getDuration());
        response.setUser(userInfo);

        return response;
    }

    @Transactional
    public TokenResponseDto refreshToken(RefreshTokenRequestDto refreshRequest) {
        String refreshTokenStr = refreshRequest.getRefreshToken();

        // Validar refresh token con JwtTokenValidator
        if (!jwtTokenValidator.validateToken(refreshTokenStr)) {
            throw new NotAuthorizedException("Refresh token inválido o expirado");
        }

        // Verificar que el token existe en base de datos y no está revocado
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new NotAuthorizedException("Refresh token no encontrado o revocado"));

        // Verificar que no esté expirado
        if (refreshToken.isExpired()) {
            throw new NotAuthorizedException("Refresh token expirado");
        }

        // Obtener usuario del token
        User user = refreshToken.getUser();

        if (!user.getActive() || user.getStatus() != UserStatus.ACTIVE) {
            throw new NotAuthorizedException("Usuario inactivo o bloqueado");
        }

        // Generar nuevo access token
        String newAccessToken = jwtTokenGenerator.generateAccessToken(user);

        // Opcionalmente generar nuevo refresh token (rotation)
        String newRefreshTokenStr = jwtTokenGenerator.generateRefreshToken(user);

        // Revocar el refresh token anterior
        refreshToken.setIsRevoked(true);
        refreshTokenRepository.saveRefreshToken(refreshToken);

        // Guardar nuevo refresh token
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken(newRefreshTokenStr);
        newRefreshToken.setUser(user);
        newRefreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(jwtTokenGenerator.getRefreshDuration()));
        newRefreshToken.setIsRevoked(false);
        refreshTokenRepository.saveRefreshToken(newRefreshToken);

        // Construir respuesta
        TokenResponseDto response = new TokenResponseDto();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshTokenStr);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtTokenGenerator.getDuration());

        return response;
    }

    @Transactional
    public void logout(String refreshToken) {
        // Invalidar el refresh token específico
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElse(null);
        
        if (token != null) {
            token.setIsRevoked(true);
            refreshTokenRepository.saveRefreshToken(token);
        }
    }

    @Transactional
    public void logoutAllDevices(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // Invalidar todos los refresh tokens del usuario
        refreshTokenRepository.revokeAllByUser(user);
    }
}