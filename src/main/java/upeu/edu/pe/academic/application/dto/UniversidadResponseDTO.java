package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UniversidadResponseDTO {

    private Long id;
    private String nombre;
    private String sigla;
    private String ruc;
    private String tipo;
    private String direccion;
    private String telefono;
    private String email;
    private String website;
    private String logoUrl;
    private String rector;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
