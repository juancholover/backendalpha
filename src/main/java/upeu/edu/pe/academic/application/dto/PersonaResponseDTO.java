package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PersonaResponseDTO {

    private Long id;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombreCompleto; // Calculado: nombres + apellidos
    private String tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaNacimiento;
    private Integer edad; // Calculada
    private String genero;
    private String estadoCivil;
    private String direccion;
    private String telefono;
    private String celular;
    private String email;
    private String fotoUrl;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
