package upeu.edu.pe.curriculum.application.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SilaboPlantillaResponseDTO {
    private Long id;
    private Long silaboId;
    private String silaboCursoNombre;
    private String silaboCursoCodigo;
    private String silaboAnioAcademico;
    private String estado;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private String autorizadoPor;
    private LocalDate fechaAutorizacion;
    private String notas;
    private String nivelFlexibilidad;
    private Integer cantidadPublicaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
