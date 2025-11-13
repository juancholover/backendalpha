package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UniversidadRequestDTO {

    @NotBlank(message = "El nombre de la universidad es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @NotBlank(message = "La sigla es obligatoria")
    @Size(max = 20, message = "La sigla no puede exceder 20 caracteres")
    private String sigla;

    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "\\d{11}", message = "El RUC debe tener 11 dígitos")
    private String ruc;

    @NotBlank(message = "El tipo de universidad es obligatorio")
    @Pattern(regexp = "PUBLICA|PRIVADA", message = "El tipo debe ser PUBLICA o PRIVADA")
    private String tipo;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Size(max = 255, message = "El sitio web no puede exceder 255 caracteres")
    private String website;

    @Size(max = 255, message = "La URL del logo no puede exceder 255 caracteres")
    private String logoUrl;

    @Size(max = 100, message = "El nombre del rector no puede exceder 100 caracteres")
    private String rector;
}
