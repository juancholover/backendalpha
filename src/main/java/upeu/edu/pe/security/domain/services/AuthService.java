package upeu.edu.pe.security.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import upeu.edu.pe.security.application.dto.*;
import upeu.edu.pe.security.domain.entities.RefreshToken;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.repositories.RefreshTokenRepository;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.security.infrastructure.utils.JwtTokenGenerator;
import upeu.edu.pe.security.infrastructure.utils.JwtTokenValidator;
import upeu.edu.pe.security.infrastructure.utils.PasswordEncoder;

import java.time.LocalDateTime;

@ApplicationScoped
public class AuthService {

    @Inject
    AuthUsuarioRepository authUsuarioRepository;

    @Inject
    JwtTokenGenerator jwtTokenGenerator;

    @Inject
    JwtTokenValidator jwtTokenValidator;

    @Inject
    PasswordEncoder passwordEncoder;

    @Inject
    RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        AuthUsuario authUsuario = authUsuarioRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new NotAuthorizedException("Credenciales inválidas"));

        // Validar contraseña con PasswordEncoder
        if (!passwordEncoder.matches(loginRequest.getPassword(), authUsuario.getPasswordHash())) {
            authUsuario.registrarAccesoFallido();
            authUsuarioRepository.persist(authUsuario);
            throw new NotAuthorizedException("Credenciales inválidas");
        }

        if (!authUsuario.estaActivo()) {
            throw new NotAuthorizedException("Usuario inactivo o bloqueado");
        }

        // Generar tokens JWT
        String accessToken = jwtTokenGenerator.generateAccessToken(authUsuario);
        String refreshTokenStr = jwtTokenGenerator.generateRefreshToken(authUsuario);

        // Guardar refresh token en base de datos
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setAuthUsuario(authUsuario);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(jwtTokenGenerator.getRefreshDuration()));
        refreshToken.setIsRevoked(false);
        refreshTokenRepository.saveRefreshToken(refreshToken);

        // Registrar acceso exitoso
        authUsuario.registrarAccesoExitoso();
        authUsuarioRepository.persist(authUsuario);

        // Construir respuesta
        AuthResponseDto.UserInfoDto userInfo = new AuthResponseDto.UserInfoDto(
            authUsuario.getId(),
            authUsuario.getUsername(),
            authUsuario.getEmail(),
            authUsuario.getPersona() != null ? authUsuario.getPersona().getNombres() : "",
            authUsuario.getPersona() != null ? authUsuario.getPersona().getApellidoPaterno() : "",
            null, // rol enum - AuthUsuario usa Rol entity
            null, // status enum - AuthUsuario usa String estado
            authUsuario.getUltimoAcceso()
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
        AuthUsuario authUsuario = refreshToken.getAuthUsuario();

        if (!authUsuario.estaActivo()) {
            throw new NotAuthorizedException("Usuario inactivo o bloqueado");
        }

        // Generar nuevo access token
        String newAccessToken = jwtTokenGenerator.generateAccessToken(authUsuario);

        // Opcionalmente generar nuevo refresh token (rotation)
        String newRefreshTokenStr = jwtTokenGenerator.generateRefreshToken(authUsuario);

        // Revocar el refresh token anterior
        refreshToken.setIsRevoked(true);
        refreshTokenRepository.saveRefreshToken(refreshToken);

        // Guardar nuevo refresh token
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken(newRefreshTokenStr);
        newRefreshToken.setAuthUsuario(authUsuario);
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
        AuthUsuario authUsuario = authUsuarioRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        // Invalidar todos los refresh tokens del usuario
        refreshTokenRepository.revokeAllByAuthUsuario(authUsuario);
    }
}