package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import upeu.edu.pe.security.application.dto.TokenResponseDto;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.entities.RefreshToken;
import upeu.edu.pe.security.domain.repositories.RefreshTokenRepository;
import upeu.edu.pe.security.infrastructure.utils.JwtTokenGenerator;
import upeu.edu.pe.security.infrastructure.utils.JwtTokenValidator;

import java.time.LocalDateTime;

@ApplicationScoped
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtTokenValidator jwtTokenValidator;

    @Inject
    public RefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository,
            JwtTokenGenerator jwtTokenGenerator,
            JwtTokenValidator jwtTokenValidator) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.jwtTokenValidator = jwtTokenValidator;
    }

    public TokenResponseDto execute(String refreshTokenStr) {
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

        // Generar nuevo refresh token (rotation)
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
}
