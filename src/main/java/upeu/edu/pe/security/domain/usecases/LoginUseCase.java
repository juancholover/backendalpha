package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import upeu.edu.pe.security.application.dto.AuthResponseDto;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.entities.RefreshToken;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.security.domain.repositories.RefreshTokenRepository;
import upeu.edu.pe.security.infrastructure.utils.JwtTokenGenerator;
import upeu.edu.pe.security.infrastructure.utils.PasswordEncoder;

import java.time.LocalDateTime;

@ApplicationScoped
public class LoginUseCase {

    private final AuthUsuarioRepository authUsuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final PasswordEncoder passwordEncoder;

    @Inject
    public LoginUseCase(AuthUsuarioRepository authUsuarioRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtTokenGenerator jwtTokenGenerator,
            PasswordEncoder passwordEncoder) {
        this.authUsuarioRepository = authUsuarioRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDto execute(String username, String password) {
        AuthUsuario authUsuario = authUsuarioRepository.findByUsername(username)
                .orElseThrow(() -> new NotAuthorizedException("Credenciales inválidas"));

        // Validar contraseña
        if (!passwordEncoder.matches(password, authUsuario.getPasswordHash())) {
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

        // Guardar refresh token
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
                authUsuario.getRol() != null ? authUsuario.getRol().getNombre() : null,
                authUsuario.getActive() ? "ACTIVO" : "INACTIVO",
                authUsuario.getUltimoAcceso());

        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenStr);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtTokenGenerator.getDuration());
        response.setUser(userInfo);

        return response;
    }
}
