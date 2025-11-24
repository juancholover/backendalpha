package upeu.edu.pe.security.domain.repositories;

import upeu.edu.pe.security.domain.entities.RefreshToken;
import upeu.edu.pe.security.domain.entities.AuthUsuario;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository {
    List<RefreshToken> getAllRefreshTokens();
    Optional<RefreshToken> getRefreshTokenById(Long id);
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByAuthUsuario(AuthUsuario authUsuario);
    RefreshToken saveRefreshToken(RefreshToken refreshToken);
    void removeRefreshTokenById(Long id);
    void revokeAllByAuthUsuario(AuthUsuario authUsuario);
}
