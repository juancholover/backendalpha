package upeu.edu.pe.security.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.security.application.dto.AuthResponseDto;
import upeu.edu.pe.security.application.dto.LoginRequestDto;
import upeu.edu.pe.security.application.dto.RefreshTokenRequestDto;
import upeu.edu.pe.security.application.dto.TokenResponseDto;
import upeu.edu.pe.security.domain.usecases.LoginUseCase;
import upeu.edu.pe.security.domain.usecases.LogoutUseCase;
import upeu.edu.pe.security.domain.usecases.RefreshTokenUseCase;

@ApplicationScoped
public class AuthApplicationService {

    @Inject
    LoginUseCase loginUseCase;

    @Inject
    RefreshTokenUseCase refreshTokenUseCase;

    @Inject
    LogoutUseCase logoutUseCase;

    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        return loginUseCase.execute(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @Transactional
    public TokenResponseDto refreshToken(RefreshTokenRequestDto refreshRequest) {
        return refreshTokenUseCase.execute(refreshRequest.getRefreshToken());
    }

    @Transactional
    public void logout(String refreshToken) {
        logoutUseCase.execute(refreshToken);
    }

    @Transactional
    public void logoutAllDevices(String username) {
        logoutUseCase.logoutAllDevices(username);
    }
}
