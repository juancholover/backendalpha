package upeu.edu.pe.security.domain.repositories.impl;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.domain.entities.RefreshToken;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.security.domain.repositories.RefreshTokenRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository, PanacheRepositoryBase<RefreshToken, Long> {

    @Override
    public List<RefreshToken> getAllRefreshTokens() {
        return listAll();
    }

    @Override
    public Optional<RefreshToken> getRefreshTokenById(Long id) {
        return findByIdOptional(id);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return find("token = ?1 and isRevoked = false and active = true", token).firstResultOptional();
    }

    @Override
    public List<RefreshToken> findByAuthUsuario(AuthUsuario authUsuario) {
        return find("authUsuario.id = ?1 and isRevoked = false and active = true", authUsuario.getId()).list();
    }

    @Override
    @Transactional
    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        persist(refreshToken);
        return refreshToken;
    }

    @Override
    @Transactional
    public void removeRefreshTokenById(Long id) {
        deleteById(id);
    }

    @Override
    @Transactional
    public void revokeAllByAuthUsuario(AuthUsuario authUsuario) {
        update("isRevoked = true where authUsuario.id = ?1 and isRevoked = false", authUsuario.getId());
    }

    /**
     * Elimina un token específico por su valor
     */
    @Transactional
    public void deleteByToken(String token) {
        delete("token", token);
    }

    /**
     * Revoca un token específico por su valor
     */
    @Transactional
    public void revokeByToken(String token) {
        update("isRevoked = true where token = ?1 and isRevoked = false", token);
    }

    /**
     * Elimina todos los tokens de un usuario
     */
    @Transactional
    public void deleteAllByAuthUsuarioId(Long authUsuarioId) {
        delete("authUsuario.id", authUsuarioId);
    }
}
