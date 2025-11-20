package upeu.edu.pe.academic.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RequisitoCursoResponseDTO {

    private Long id;
    private Long cursoId;
    private String cursoNombre;
    private String cursoCodigo;
    private Long cursoRequisitoId;
    private String cursoRequisitoNombre;
    private String cursoRequisitoCodigo;
    private String tipoRequisito;
    private Boolean esObligatorio;
    private BigDecimal notaMinimaRequerida;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;

    // Constructors
    public RequisitoCursoResponseDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
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

    public Long getCursoRequisitoId() {
        return cursoRequisitoId;
    }

    public void setCursoRequisitoId(Long cursoRequisitoId) {
        this.cursoRequisitoId = cursoRequisitoId;
    }

    public String getCursoRequisitoNombre() {
        return cursoRequisitoNombre;
    }

    public void setCursoRequisitoNombre(String cursoRequisitoNombre) {
        this.cursoRequisitoNombre = cursoRequisitoNombre;
    }

    public String getCursoRequisitoCodigo() {
        return cursoRequisitoCodigo;
    }

    public void setCursoRequisitoCodigo(String cursoRequisitoCodigo) {
        this.cursoRequisitoCodigo = cursoRequisitoCodigo;
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
