package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class RequisitoCursoRequestDTO {

    @NotNull(message = "El ID de la universidad es obligatorio")
    private Long universidadId;

    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;

    @NotNull(message = "El ID del curso requisito es obligatorio")
    private Long cursoRequisitoId;

    @NotBlank(message = "El tipo de requisito es obligatorio")
    private String tipoRequisito;

    @NotNull(message = "El campo esObligatorio es obligatorio")
    private Boolean esObligatorio;

    @Min(value = 0, message = "La nota mínima debe ser mayor o igual a 0")
    private Integer notaMinimaRequerida;

    private String observacion;

    
    public RequisitoCursoRequestDTO() {
    }

    public RequisitoCursoRequestDTO(Long universidadId, Long cursoId, Long cursoRequisitoId, String tipoRequisito, Boolean esObligatorio) {
        this.universidadId = universidadId;
        this.cursoId = cursoId;
        this.cursoRequisitoId = cursoRequisitoId;
        this.tipoRequisito = tipoRequisito;
        this.esObligatorio = esObligatorio;
    }

    
    public Long getUniversidadId() {
        return universidadId;
    }

    public void setUniversidadId(Long universidadId) {
        this.universidadId = universidadId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public Long getCursoRequisitoId() {
        return cursoRequisitoId;
    }

    public void setCursoRequisitoId(Long cursoRequisitoId) {
        this.cursoRequisitoId = cursoRequisitoId;
    }

    public String getTipoRequisito() {
        return tipoRequisito;
    }

    public void setTipoRequisito(String tipoRequisito) {
        this.tipoRequisito = tipoRequisito;
    }

    public Boolean getEsObligatorio() {
        return esObligatorio;
    }

    public void setEsObligatorio(Boolean esObligatorio) {
        this.esObligatorio = esObligatorio;
    }

    public Integer getNotaMinimaRequerida() {
        return notaMinimaRequerida;
    }

    public void setNotaMinimaRequerida(Integer notaMinimaRequerida) {
        this.notaMinimaRequerida = notaMinimaRequerida;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
