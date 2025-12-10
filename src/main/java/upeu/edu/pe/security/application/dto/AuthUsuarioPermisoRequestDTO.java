package upeu.edu.pe.security.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO para asignar un permiso individual a un usuario
 */
public record AuthUsuarioPermisoRequestDTO(
    
    @NotNull(message = "El ID del usuario es obligatorio")
    Long authUsuarioId,
    
    @NotNull(message = "El ID del permiso es obligatorio")
    Long permisoId,
    
    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    String motivo,
    
    LocalDateTime fechaExpiracion, // Opcional: si es null, el permiso es permanente
    
    Boolean esAdicion // Opcional: si es null, por defecto true (otorga permiso adicional)
) {}
