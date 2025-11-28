package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.entities.RefreshToken;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.security.domain.repositories.RefreshTokenRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUsuarioRepository authUsuarioRepository;

    @Inject
    public LogoutUseCase(RefreshTokenRepository refreshTokenRepository,
            AuthUsuarioRepository authUsuarioRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authUsuarioRepository = authUsuarioRepository;
    }

    public void execute(String refreshTokenStr) {
        // Invalidar el refresh token específico
        RefreshToken token = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElse(null);

        if (token != null) {
            token.setIsRevoked(true);
            refreshTokenRepository.saveRefreshToken(token);
        }
    }

    public void logoutAllDevices(String username) {
        AuthUsuario authUsuario = authUsuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("AuthUsuario", "username", username));

        // Invalidar todos los refresh tokens del usuario
        refreshTokenRepository.revokeAllByAuthUsuario(authUsuario);
    }
}
