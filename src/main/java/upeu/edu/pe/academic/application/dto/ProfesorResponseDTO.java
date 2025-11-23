package upeu.edu.pe.academic.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta con información completa del profesor
 * Incluye datos del empleado, persona y años de servicio
 */
public class ProfesorResponseDTO {

    private Long id;
    
    // Datos del empleado
    private Long empleadoId;
    private String codigoEmpleado;
    private String cargoEmpleado;
    
    // Datos de la persona
    private Long personaId;
    private String nombreCompleto;
    private String tipoDocumento;
    private String numeroDocumento;
    private String email;
    private String telefono;
    
    // Datos académicos del profesor
    private String especialidad;
    private String gradoAcademico;
    private String tituloProfesional;
    private String universidadProcedencia;
    
    // Datos de categorización
    private String categoriaDocente;
    private String dedicacion;
    private LocalDate fechaIngresoDocente;
    private Integer aniosServicio;
    
    // Datos de investigación
    private String codigoRenacyt;
    private String codigoOrcid;
    private String codigoCtiVitae;
    private String areasInvestigacion;
    private Integer numeroPublicaciones;
    private Integer numeroProyectos;
    
    // Datos administrativos
    private String observaciones;
    private Boolean active;
    
    // Auditoría
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;


    public ProfesorResponseDTO() {
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }

    public String getCargoEmpleado() {
        return cargoEmpleado;
    }

    public void setCargoEmpleado(String cargoEmpleado) {
        this.cargoEmpleado = cargoEmpleado;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getGradoAcademico() {
        return gradoAcademico;
    }

    public void setGradoAcademico(String gradoAcademico) {
        this.gradoAcademico = gradoAcademico;
    }

    public String getTituloProfesional() {
        return tituloProfesional;
    }

    public void setTituloProfesional(String tituloProfesional) {
        this.tituloProfesional = tituloProfesional;
    }

    public String getUniversidadProcedencia() {
        return universidadProcedencia;
    }

    public void setUniversidadProcedencia(String universidadProcedencia) {
        this.universidadProcedencia = universidadProcedencia;
    }

    public String getCategoriaDocente() {
        return categoriaDocente;
    }

    public void setCategoriaDocente(String categoriaDocente) {
        this.categoriaDocente = categoriaDocente;
    }

    public String getDedicacion() {
        return dedicacion;
    }

    public void setDedicacion(String dedicacion) {
        this.dedicacion = dedicacion;
    }

    public LocalDate getFechaIngresoDocente() {
        return fechaIngresoDocente;
    }

    public void setFechaIngresoDocente(LocalDate fechaIngresoDocente) {
        this.fechaIngresoDocente = fechaIngresoDocente;
    }

    public Integer getAniosServicio() {
        return aniosServicio;
    }

    public void setAniosServicio(Integer aniosServicio) {
        this.aniosServicio = aniosServicio;
    }

    public String getCodigoRenacyt() {
        return codigoRenacyt;
    }

    public void setCodigoRenacyt(String codigoRenacyt) {
        this.codigoRenacyt = codigoRenacyt;
    }

    public String getCodigoOrcid() {
        return codigoOrcid;
    }

    public void setCodigoOrcid(String codigoOrcid) {
        this.codigoOrcid = codigoOrcid;
    }

    public String getCodigoCtiVitae() {
        return codigoCtiVitae;
    }

    public void setCodigoCtiVitae(String codigoCtiVitae) {
        this.codigoCtiVitae = codigoCtiVitae;
    }

    public String getAreasInvestigacion() {
        return areasInvestigacion;
    }

    public void setAreasInvestigacion(String areasInvestigacion) {
        this.areasInvestigacion = areasInvestigacion;
    }

    public Integer getNumeroPublicaciones() {
        return numeroPublicaciones;
    }

    public void setNumeroPublicaciones(Integer numeroPublicaciones) {
        this.numeroPublicaciones = numeroPublicaciones;
    }

    public Integer getNumeroProyectos() {
        return numeroProyectos;
    }

    public void setNumeroProyectos(Integer numeroProyectos) {
        this.numeroProyectos = numeroProyectos;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
