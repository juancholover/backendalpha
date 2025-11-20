package upeu.edu.pe.academic.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class RequisitoCursoRequestDTO {

    @NotNull(message = "El ID del curso es obligatorio")
    private Long cursoId;

    @NotNull(message = "El ID del curso requisito es obligatorio")
    private Long cursoRequisitoId;

    @NotBlank(message = "El tipo de requisito es obligatorio")
    private String tipoRequisito;

    @NotNull(message = "El campo esObligatorio es obligatorio")
    private Boolean esObligatorio;

    @DecimalMin(value = "0.0", message = "La nota mínima debe ser mayor o igual a 0")
    private BigDecimal notaMinimaRequerida;

    // Constructors
    public RequisitoCursoRequestDTO() {
    }

    public RequisitoCursoRequestDTO(Long cursoId, Long cursoRequisitoId, String tipoRequisito, Boolean esObligatorio) {
        this.cursoId = cursoId;
        this.cursoRequisitoId = cursoRequisitoId;
        this.tipoRequisito = tipoRequisito;
        this.esObligatorio = esObligatorio;
    }

    // Getters and Setters
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

    public BigDecimal getNotaMinimaRequerida() {
        return notaMinimaRequerida;
    }

    public void setNotaMinimaRequerida(BigDecimal notaMinimaRequerida) {
        this.notaMinimaRequerida = notaMinimaRequerida;
    }
}
