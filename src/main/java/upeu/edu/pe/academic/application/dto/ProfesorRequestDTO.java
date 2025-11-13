package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * DTO para crear y actualizar profesores
 * Validaciones: empleado, grado académico, categoría docente, dedicación
 */
public class ProfesorRequestDTO {

    @NotNull(message = "El empleado es obligatorio")
    private Long empleadoId;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 200, message = "La especialidad no puede exceder 200 caracteres")
    private String especialidad;

    @NotBlank(message = "El grado académico es obligatorio")
    @Pattern(
        regexp = "^(BACHILLER|LICENCIADO|MAGISTER|DOCTOR)$",
        message = "El grado académico debe ser: BACHILLER, LICENCIADO, MAGISTER o DOCTOR"
    )
    private String gradoAcademico;

    @Size(max = 200, message = "El título profesional no puede exceder 200 caracteres")
    private String tituloProfesional;

    @Size(max = 200, message = "La universidad de procedencia no puede exceder 200 caracteres")
    private String universidadProcedencia;

    @NotBlank(message = "La categoría docente es obligatoria")
    @Pattern(
        regexp = "^(AUXILIAR|ASOCIADO|PRINCIPAL)$",
        message = "La categoría docente debe ser: AUXILIAR, ASOCIADO o PRINCIPAL"
    )
    private String categoriaDocente;

    @NotBlank(message = "La dedicación es obligatoria")
    @Pattern(
        regexp = "^(TC|TP|PH)$",
        message = "La dedicación debe ser: TC (Tiempo Completo), TP (Tiempo Parcial) o PH (Por Horas)"
    )
    private String dedicacion;

    @NotNull(message = "La fecha de ingreso como docente es obligatoria")
    @PastOrPresent(message = "La fecha de ingreso no puede ser futura")
    private LocalDate fechaIngresoDocente;

    @Size(max = 100, message = "El código RENACYT no puede exceder 100 caracteres")
    private String codigoRenacyt;

    @Size(max = 100, message = "El código ORCID no puede exceder 100 caracteres")
    private String codigoOrcid;

    @Size(max = 100, message = "El código CTI Vitae no puede exceder 100 caracteres")
    private String codigoCtiVitae;

    @Size(max = 500, message = "Las áreas de investigación no pueden exceder 500 caracteres")
    private String areasInvestigacion;

    @Min(value = 0, message = "El número de publicaciones no puede ser negativo")
    private Integer numeroPublicaciones;

    @Min(value = 0, message = "El número de proyectos no puede ser negativo")
    private Integer numeroProyectos;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    // Constructors
    public ProfesorRequestDTO() {
        this.numeroPublicaciones = 0;
        this.numeroProyectos = 0;
    }

    // Getters and Setters
    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
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
}
