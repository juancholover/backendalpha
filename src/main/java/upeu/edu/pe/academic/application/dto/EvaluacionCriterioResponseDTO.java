package upeu.edu.pe.academic.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EvaluacionCriterioResponseDTO {

    private Long id;
    private Long seccionId;
    private String seccionCodigo;
    private String cursoNombre;
    private String nombre;
    private Integer peso;
    private String tipoEvaluacion;
    private BigDecimal notaMaxima;
    private BigDecimal notaMinimaAprobatoria;
    private Boolean esRecuperable;
    private Integer orden;
    private String estado;
    private String descripcion;
    private Integer cantidadNotas;
    private Double promedioNotas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;

    // Constructors
    public EvaluacionCriterioResponseDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }

    public String getSeccionCodigo() {
        return seccionCodigo;
    }

    public void setSeccionCodigo(String seccionCodigo) {
        this.seccionCodigo = seccionCodigo;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getPeso() {
        return peso;
    }

    public void setPeso(Integer peso) {
        this.peso = peso;
    }

    public String getTipoEvaluacion() {
        return tipoEvaluacion;
    }

    public void setTipoEvaluacion(String tipoEvaluacion) {
        this.tipoEvaluacion = tipoEvaluacion;
    }

    public BigDecimal getNotaMaxima() {
        return notaMaxima;
    }

    public void setNotaMaxima(BigDecimal notaMaxima) {
        this.notaMaxima = notaMaxima;
    }

    public BigDecimal getNotaMinimaAprobatoria() {
        return notaMinimaAprobatoria;
    }

    public void setNotaMinimaAprobatoria(BigDecimal notaMinimaAprobatoria) {
        this.notaMinimaAprobatoria = notaMinimaAprobatoria;
    }

    public Boolean getEsRecuperable() {
        return esRecuperable;
    }

    public void setEsRecuperable(Boolean esRecuperable) {
        this.esRecuperable = esRecuperable;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidadNotas() {
        return cantidadNotas;
    }

    public void setCantidadNotas(Integer cantidadNotas) {
        this.cantidadNotas = cantidadNotas;
    }

    public Double getPromedioNotas() {
        return promedioNotas;
    }

    public void setPromedioNotas(Double promedioNotas) {
        this.promedioNotas = promedioNotas;
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
