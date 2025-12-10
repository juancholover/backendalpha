package upeu.edu.pe.security.application.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para permisos individuales de usuario
 */
public record AuthUsuarioPermisoResponseDTO(
    Long id,
    Long authUsuarioId,
    String nombreUsuario, // Nombre completo del usuario
    Long permisoId,
    String codigoPermiso,
    String nombrePermiso,
    LocalDateTime fechaAsignacion,
    LocalDateTime fechaExpiracion,
    String motivo,
    Long asignadoPorId,
    String asignadoPorNombre,
    Boolean esAdicion,
    Boolean estaVigente,
    Boolean estaExpirado,
    LocalDateTime creadoEn,
    String creadoPor
) {}
