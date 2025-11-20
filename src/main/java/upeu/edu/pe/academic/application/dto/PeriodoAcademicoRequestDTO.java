package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class PeriodoAcademicoRequestDTO {

    @NotNull(message = "El ID de la universidad es obligatorio")
    private Long universidadId;

    @NotBlank(message = "El código del período es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    private String codigoPeriodo;

    @NotBlank(message = "El nombre del período es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotNull(message = "El año es obligatorio")
    private Integer anio;

    @NotNull(message = "El número de período es obligatorio")
    private Integer numeroPeriodo;

    @NotBlank(message = "El tipo de período es obligatorio")
    private String tipoPeriodo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    private LocalDate fechaInicioMatricula;
    private LocalDate fechaFinMatricula;
    private LocalDate fechaInicioClases;
    private LocalDate fechaFinClases;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    private Boolean esActual = false;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    // Constructors
    public PeriodoAcademicoRequestDTO() {
    }

    // Getters and Setters
    public Long getUniversidadId() {
        return universidadId;
    }

    public void setUniversidadId(Long universidadId) {
        this.universidadId = universidadId;
    }

    public String getCodigoPeriodo() {
        return codigoPeriodo;
    }

    public void setCodigoPeriodo(String codigoPeriodo) {
        this.codigoPeriodo = codigoPeriodo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getNumeroPeriodo() {
        return numeroPeriodo;
    }

    public void setNumeroPeriodo(Integer numeroPeriodo) {
        this.numeroPeriodo = numeroPeriodo;
    }

    public String getTipoPeriodo() {
        return tipoPeriodo;
    }

    public void setTipoPeriodo(String tipoPeriodo) {
        this.tipoPeriodo = tipoPeriodo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDate getFechaInicioMatricula() {
        return fechaInicioMatricula;
    }

    public void setFechaInicioMatricula(LocalDate fechaInicioMatricula) {
        this.fechaInicioMatricula = fechaInicioMatricula;
    }

    public LocalDate getFechaFinMatricula() {
        return fechaFinMatricula;
    }

    public void setFechaFinMatricula(LocalDate fechaFinMatricula) {
        this.fechaFinMatricula = fechaFinMatricula;
    }

    public LocalDate getFechaInicioClases() {
        return fechaInicioClases;
    }

    public void setFechaInicioClases(LocalDate fechaInicioClases) {
        this.fechaInicioClases = fechaInicioClases;
    }

    public LocalDate getFechaFinClases() {
        return fechaFinClases;
    }

    public void setFechaFinClases(LocalDate fechaFinClases) {
        this.fechaFinClases = fechaFinClases;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getEsActual() {
        return esActual;
    }

    public void setEsActual(Boolean esActual) {
        this.esActual = esActual;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
