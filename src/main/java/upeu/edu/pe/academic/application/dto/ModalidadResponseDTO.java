package upeu.edu.pe.academic.application.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ModalidadResponseDTO {
    private Long id;
    private Long universidadId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Boolean requiereAula;
    private Boolean requierePlataforma;
    private Integer porcentajePresencialidad;
    private String colorHex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
