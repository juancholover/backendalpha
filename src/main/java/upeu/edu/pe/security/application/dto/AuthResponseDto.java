package upeu.edu.pe.security.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import upeu.edu.pe.permissions.application.dto.PermissionsResponseDTO;

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
    private String issuedAt;
    private UserInfoDto user;
    private PermissionsResponseDTO permissions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDto {
        private Long idPersona;
        private String documentoIdentidad;
        private String nombre;
        private String apellidos;
        private String nombreCompleto;
        private String email;
        private String telefono;
        private String fotoUrl;
        private List<String> rolesBase;
        private String estadoCuenta;
        private Boolean requiereCambioPassword;
        private LocalDateTime ultimaSesion;
    }
}
