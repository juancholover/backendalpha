package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import java.math.BigDecimal;

public class EvaluacionNotaRequestDTO {

    @NotNull(message = "El ID de la matrícula es obligatorio")
    private Long matriculaId;

    @NotNull(message = "El ID del criterio es obligatorio")
    private Long criterioId;

    @DecimalMin(value = "0.00", message = "La nota debe ser mayor o igual a 0")
    @DecimalMax(value = "20.00", message = "La nota debe ser menor o igual a 20")
    private BigDecimal nota;

    @DecimalMin(value = "0.00", message = "La nota de recuperación debe ser mayor o igual a 0")
    @DecimalMax(value = "20.00", message = "La nota de recuperación debe ser menor o igual a 20")
    private BigDecimal notaRecuperacion;

    @NotNull(message = "El estado es obligatorio")
    private String estado;

    private String observaciones;


    public EvaluacionNotaRequestDTO() {
    }

    
    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getCriterioId() {
        return criterioId;
    }

    public void setCriterioId(Long criterioId) {
        this.criterioId = criterioId;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }

    public BigDecimal getNotaRecuperacion() {
        return notaRecuperacion;
    }

    public void setNotaRecuperacion(BigDecimal notaRecuperacion) {
        this.notaRecuperacion = notaRecuperacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
