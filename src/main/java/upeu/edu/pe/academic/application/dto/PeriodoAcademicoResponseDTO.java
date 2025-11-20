package upeu.edu.pe.academic.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PeriodoAcademicoResponseDTO {

    private Long id;
    private Long universidadId;
    private String universidadNombre;
    private String codigoPeriodo;
    private String nombre;
    private Integer anio;
    private Integer numeroPeriodo;
    private String tipoPeriodo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaInicioMatricula;
    private LocalDate fechaFinMatricula;
    private LocalDate fechaInicioClases;
    private LocalDate fechaFinClases;
    private String estado;
    private Boolean esActual;
    private String descripcion;
    private Integer cantidadSecciones;
    private Integer cantidadMatriculas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;

    // Constructors
    public PeriodoAcademicoResponseDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUniversidadId() {
        return universidadId;
    }

    public void setUniversidadId(Long universidadId) {
        this.universidadId = universidadId;
    }

    public String getUniversidadNombre() {
        return universidadNombre;
    }

    public void setUniversidadNombre(String universidadNombre) {
        this.universidadNombre = universidadNombre;
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

    public Integer getCantidadSecciones() {
        return cantidadSecciones;
    }

    public void setCantidadSecciones(Integer cantidadSecciones) {
        this.cantidadSecciones = cantidadSecciones;
    }

    public Integer getCantidadMatriculas() {
        return cantidadMatriculas;
    }

    public void setCantidadMatriculas(Integer cantidadMatriculas) {
        this.cantidadMatriculas = cantidadMatriculas;
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
