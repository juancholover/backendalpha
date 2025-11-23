package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UniversidadRequestDTO {

    @NotBlank(message = "El código de la universidad es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre de la universidad es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombre;

    @NotBlank(message = "El dominio es obligatorio")
    @Size(max = 50, message = "El dominio no puede exceder 50 caracteres")
    @Pattern(regexp = "^[a-z0-9.-]+$", message = "El dominio debe contener solo letras minúsculas, números, puntos y guiones")
    private String dominio;

    @NotBlank(message = "El RUC es obligatorio")
    @Size(min = 11, max = 11, message = "El RUC debe tener exactamente 11 dígitos")
    @Pattern(regexp = "^[0-9]{11}$", message = "El RUC debe contener solo números")
    private String ruc;

    @NotBlank(message = "El tipo de universidad es obligatorio")
    @Pattern(regexp = "^(PUBLICA|PRIVADA|CONSORCIO)$", message = "El tipo debe ser PUBLICA, PRIVADA o CONSORCIO")
    private String tipo;

    @Size(max = 255, message = "El website no puede exceder 255 caracteres")
    @Pattern(regexp = "^(https?://)?[a-z0-9.-]+\\.[a-z]{2,}.*$", message = "El website debe ser una URL válida", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String website;

    @Size(max = 500, message = "La URL del logo no puede exceder 500 caracteres")
    private String logoUrl;

    @Size(max = 50, message = "La zona horaria no puede exceder 50 caracteres")
    private String zonaHoraria;

    @Size(max = 20, message = "El locale no puede exceder 20 caracteres")
    private String locale;

    private String configuracion;

    // ==================== CAMPOS SAAS ====================

    @Pattern(regexp = "^(FREE|BASIC|PREMIUM|ENTERPRISE)$", message = "El plan debe ser FREE, BASIC, PREMIUM o ENTERPRISE")
    private String plan;

    @Pattern(regexp = "^(ACTIVA|SUSPENDIDA|TRIAL|VENCIDA)$", message = "El estado debe ser ACTIVA, SUSPENDIDA, TRIAL o VENCIDA")
    private String estado;

    private java.time.LocalDate fechaVencimiento;

    @Min(value = 1, message = "El máximo de estudiantes debe ser mayor a 0")
    private Integer maxEstudiantes;

    @Min(value = 1, message = "El máximo de docentes debe ser mayor a 0")
    private Integer maxDocentes;

    @Min(value = 1, message = "Los créditos máximos por ciclo deben ser mayor a 0")
    private Integer creditosMaximosPorCiclo;

    @Min(value = 1, message = "Los créditos mínimos de tiempo completo deben ser mayor a 0")
    private Integer creditosMinimosTiempoCompleto;

    @Min(value = 1, message = "La duración del ciclo en meses debe ser mayor a 0")
    private Integer duracionCicloMeses;
}
