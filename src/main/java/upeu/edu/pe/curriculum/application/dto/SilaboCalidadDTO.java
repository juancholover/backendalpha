package upeu.edu.pe.curriculum.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de evaluación de calidad de sílabos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SilaboCalidadDTO {
    private Long id;
    private Long silaboId;
    private Integer puntajeTotal;
    private String evaluadoPor;
    private LocalDateTime fechaEvaluacion;
    
    // Scores individuales
    private Integer descripcionScore;
    private Integer objetivosScore;
    private Integer bibliografiaScore;
    private Integer evaluacionesScore;
    private Integer metodologiaScore;
    private Integer competenciasScore;
    private Integer unidadesScore;
    private Integer actividadesScore;
    
    private Boolean aprobado;
    private String observaciones;
}
