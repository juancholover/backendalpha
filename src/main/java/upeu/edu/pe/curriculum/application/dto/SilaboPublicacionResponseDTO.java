package upeu.edu.pe.curriculum.application.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SilaboPublicacionResponseDTO {
    private Long id;
    private Long plantillaId;
    private String plantillaEstado;
    private Long localizacionId;
    private String localizacionNombre;
    private String localizacionCodigo;
    private Long silaboCampusId;
    private String anioAcademico;
    private String estado;
    private LocalDate fechaPublicacion;
    private String publicadoPor;
    private LocalDate fechaInicioCampus;
    private LocalDate fechaFinCampus;
    private Boolean adaptado;
    private String adaptadoPor;
    private LocalDate fechaAdaptacion;
    private String notasAdaptacion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
