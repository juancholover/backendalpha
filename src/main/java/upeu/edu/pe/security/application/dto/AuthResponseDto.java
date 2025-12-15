package upeu.edu.pe.security.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserInfoDto user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDto {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private List<String> roles; // Roles de Casbin
        private String status; // Estado: "ACTIVE" o "INACTIVE" desde AuthUsuario.active
        private LocalDateTime lastLogin;
    }
}