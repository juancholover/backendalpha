package upeu.edu.pe.academic.application.dto;

import java.time.LocalDateTime;

public class CursoOfertadoResponseDTO {

    private Long id;
    private Long universidadId;
    private String universidadNombre;
    private Long planAcademicoId;
    private String cursoNombre;
    private String cursoCodigo;
    private Long periodoAcademicoId;
    private String periodoNombre;
    private String periodoCodigo;
    private Long profesorId;
    private String profesorNombre;
    private Long localizacionId;
    private String localizacionNombre;
    private String codigoSeccion;
    private Integer capacidadMaxima;
    private Integer vacantesDisponibles;
    private String modalidad;
    private String estado;
    private String observaciones;
    private Integer cantidadMatriculados;
    private Integer cantidadCriterios;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;

    // Constructors
    public CursoOfertadoResponseDTO() {
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

    public Long getPlanAcademicoId() {
        return planAcademicoId;
    }

    public void setPlanAcademicoId(Long planAcademicoId) {
        this.planAcademicoId = planAcademicoId;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public String getCursoCodigo() {
        return cursoCodigo;
    }

    public void setCursoCodigo(String cursoCodigo) {
        this.cursoCodigo = cursoCodigo;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public String getPeriodoNombre() {
        return periodoNombre;
    }

    public void setPeriodoNombre(String periodoNombre) {
        this.periodoNombre = periodoNombre;
    }

    public String getPeriodoCodigo() {
        return periodoCodigo;
    }

    public void setPeriodoCodigo(String periodoCodigo) {
        this.periodoCodigo = periodoCodigo;
    }

    public Long getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }

    public String getProfesorNombre() {
        return profesorNombre;
    }

    public void setProfesorNombre(String profesorNombre) {
        this.profesorNombre = profesorNombre;
    }

    public Long getLocalizacionId() {
        return localizacionId;
    }

    public void setLocalizacionId(Long localizacionId) {
        this.localizacionId = localizacionId;
    }

    public String getLocalizacionNombre() {
        return localizacionNombre;
    }

    public void setLocalizacionNombre(String localizacionNombre) {
        this.localizacionNombre = localizacionNombre;
    }

    public String getCodigoSeccion() {
        return codigoSeccion;
    }

    public void setCodigoSeccion(String codigoSeccion) {
        this.codigoSeccion = codigoSeccion;
    }

    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public Integer getVacantesDisponibles() {
        return vacantesDisponibles;
    }

    public void setVacantesDisponibles(Integer vacantesDisponibles) {
        this.vacantesDisponibles = vacantesDisponibles;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
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

    public Integer getCantidadMatriculados() {
        return cantidadMatriculados;
    }

    public void setCantidadMatriculados(Integer cantidadMatriculados) {
        this.cantidadMatriculados = cantidadMatriculados;
    }

    public Integer getCantidadCriterios() {
        return cantidadCriterios;
    }

    public void setCantidadCriterios(Integer cantidadCriterios) {
        this.cantidadCriterios = cantidadCriterios;
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
