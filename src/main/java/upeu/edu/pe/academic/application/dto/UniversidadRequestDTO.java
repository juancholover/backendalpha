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

    private Long localizacionPrincipalId; // FK a Localizacion (sede principal)

    @Size(max = 50, message = "La zona horaria no puede exceder 50 caracteres")
    private String zonaHoraria;

    @Size(max = 20, message = "El locale no puede exceder 20 caracteres")
    private String locale;

    private String configuracion;
}
