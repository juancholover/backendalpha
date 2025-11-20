package upeu.edu.pe.academic.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EvaluacionNotaResponseDTO {

    private Long id;
    private Long matriculaId;
    private Long estudianteId;
    private String estudianteNombre;
    private String estudianteCodigo;
    private Long criterioId;
    private String criterioNombre;
    private Integer criterioPeso;
    private BigDecimal nota;
    private BigDecimal notaRecuperacion;
    private BigDecimal notaFinal;
    private String estado;
    private Boolean estaAprobado;
    private String observaciones;
    private LocalDateTime fechaCalificacion;
    private String calificadoPor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;

    // Constructors
    public EvaluacionNotaResponseDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteNombre() {
        return estudianteNombre;
    }

    public void setEstudianteNombre(String estudianteNombre) {
        this.estudianteNombre = estudianteNombre;
    }

    public String getEstudianteCodigo() {
        return estudianteCodigo;
    }

    public void setEstudianteCodigo(String estudianteCodigo) {
        this.estudianteCodigo = estudianteCodigo;
    }

    public Long getCriterioId() {
        return criterioId;
    }

    public void setCriterioId(Long criterioId) {
        this.criterioId = criterioId;
    }

    public String getCriterioNombre() {
        return criterioNombre;
    }

    public void setCriterioNombre(String criterioNombre) {
        this.criterioNombre = criterioNombre;
    }

    public Integer getCriterioPeso() {
        return criterioPeso;
    }

    public void setCriterioPeso(Integer criterioPeso) {
        this.criterioPeso = criterioPeso;
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

    public BigDecimal getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(BigDecimal notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getEstaAprobado() {
        return estaAprobado;
    }

    public void setEstaAprobado(Boolean estaAprobado) {
        this.estaAprobado = estaAprobado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaCalificacion() {
        return fechaCalificacion;
    }

    public void setFechaCalificacion(LocalDateTime fechaCalificacion) {
        this.fechaCalificacion = fechaCalificacion;
    }

    public String getCalificadoPor() {
        return calificadoPor;
    }

    public void setCalificadoPor(String calificadoPor) {
        this.calificadoPor = calificadoPor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
