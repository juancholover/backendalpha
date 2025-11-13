package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PersonaRequestDTO {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 50, message = "El apellido paterno no puede exceder 50 caracteres")
    private String apellidoPaterno;

    @NotBlank(message = "El apellido materno es obligatorio")
    @Size(max = 50, message = "El apellido materno no puede exceder 50 caracteres")
    private String apellidoMaterno;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "DNI|CE|PASAPORTE", message = "El tipo de documento debe ser DNI, CE o PASAPORTE")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El número de documento no puede exceder 20 caracteres")
    private String numeroDocumento;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El género es obligatorio")
    @Pattern(regexp = "M|F", message = "El género debe ser M o F")
    private String genero;

    @Size(max = 20, message = "El estado civil no puede exceder 20 caracteres")
    private String estadoCivil;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    private String telefono;

    @Size(max = 15, message = "El celular no puede exceder 15 caracteres")
    private String celular;

    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @Size(max = 255, message = "La URL de la foto no puede exceder 255 caracteres")
    private String fotoUrl;
}
