package upeu.edu.pe.academic.application.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SilaboUnidadResponseDTO {
    private Long id;
    private Long silaboId;
    private Integer numeroUnidad;
    private String titulo;
    private Integer semanaInicio;
    private Integer semanaFin;
    private Integer duracionSemanas;
    private String contenidos;
    private String logroAprendizaje;
    private String estrategiasEnsenanza;
    private Integer cantidadActividades;
    private List<SilaboActividadResponseDTO> actividades;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
