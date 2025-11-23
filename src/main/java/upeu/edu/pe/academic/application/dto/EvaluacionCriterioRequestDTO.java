package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class EvaluacionCriterioRequestDTO {

    @NotNull(message = "El ID de la universidad es obligatorio")
    private Long universidadId;

    @NotNull(message = "El ID de la sección es obligatorio")
    private Long seccionId;

    @NotBlank(message = "El nombre del criterio es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotNull(message = "El peso es obligatorio")
    @Min(value = 0, message = "El peso mínimo es 0")
    @Max(value = 100, message = "El peso máximo es 100")
    private Integer peso;

    @NotBlank(message = "El tipo de evaluación es obligatorio")
    @Size(max = 50, message = "El tipo no puede exceder 50 caracteres")
    private String tipoEvaluacion;

    @NotNull(message = "La nota máxima es obligatoria")
    @Min(value = 0, message = "La nota máxima debe ser mayor o igual a 0")
    private Integer notaMaxima;

    @NotNull(message = "La nota mínima aprobatoria es obligatoria")
    @Min(value = 0, message = "La nota mínima debe ser mayor o igual a 0")
    private Integer notaMinimaAprobatoria;

    private Boolean esRecuperable = false;

    private Integer orden;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    public EvaluacionCriterioRequestDTO() {
    }

    public Long getUniversidadId() {
        return universidadId;
    }

    public void setUniversidadId(Long universidadId) {
        this.universidadId = universidadId;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
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

    public Integer getNotaMaxima() {
        return notaMaxima;
    }

    public void setNotaMaxima(Integer notaMaxima) {
        this.notaMaxima = notaMaxima;
    }

    public Integer getNotaMinimaAprobatoria() {
        return notaMinimaAprobatoria;
    }

    public void setNotaMinimaAprobatoria(Integer notaMinimaAprobatoria) {
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
}
