package upeu.edu.pe.finance.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoDetalleDeudaResponseDTO {

    private Long id;
    private Long pagoId;
    private String pagoNumeroRecibo;
    private BigDecimal pagoMontoPagado;
    private Long deudaId;
    private String deudaNumeroDocumento;
    private String deudaConcepto;
    private BigDecimal deudaMonto;
    private BigDecimal montoAplicado;
    private String aplicadoPor;
    private LocalDateTime fechaAplicacion;
    private String estado;
    private LocalDateTime fechaReversion;
    private String motivoReversion;
    private String revertidoPor;
    private LocalDateTime createdAt;
    private Boolean active;

    // Constructors
    public PagoDetalleDeudaResponseDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPagoId() {
        return pagoId;
    }

    public void setPagoId(Long pagoId) {
        this.pagoId = pagoId;
    }

    public String getPagoNumeroRecibo() {
        return pagoNumeroRecibo;
    }

    public void setPagoNumeroRecibo(String pagoNumeroRecibo) {
        this.pagoNumeroRecibo = pagoNumeroRecibo;
    }

    public BigDecimal getPagoMontoPagado() {
        return pagoMontoPagado;
    }

    public void setPagoMontoPagado(BigDecimal pagoMontoPagado) {
        this.pagoMontoPagado = pagoMontoPagado;
    }

    public Long getDeudaId() {
        return deudaId;
    }

    public void setDeudaId(Long deudaId) {
        this.deudaId = deudaId;
    }

    public String getDeudaNumeroDocumento() {
        return deudaNumeroDocumento;
    }

    public void setDeudaNumeroDocumento(String deudaNumeroDocumento) {
        this.deudaNumeroDocumento = deudaNumeroDocumento;
    }

    public String getDeudaConcepto() {
        return deudaConcepto;
    }

    public void setDeudaConcepto(String deudaConcepto) {
        this.deudaConcepto = deudaConcepto;
    }

    public BigDecimal getDeudaMonto() {
        return deudaMonto;
    }

    public void setDeudaMonto(BigDecimal deudaMonto) {
        this.deudaMonto = deudaMonto;
    }

    public BigDecimal getMontoAplicado() {
        return montoAplicado;
    }

    public void setMontoAplicado(BigDecimal montoAplicado) {
        this.montoAplicado = montoAplicado;
    }

    public String getAplicadoPor() {
        return aplicadoPor;
    }

    public void setAplicadoPor(String aplicadoPor) {
        this.aplicadoPor = aplicadoPor;
    }

    public LocalDateTime getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDateTime fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaReversion() {
        return fechaReversion;
    }

    public void setFechaReversion(LocalDateTime fechaReversion) {
        this.fechaReversion = fechaReversion;
    }

    public String getMotivoReversion() {
        return motivoReversion;
    }

    public void setMotivoReversion(String motivoReversion) {
        this.motivoReversion = motivoReversion;
    }

    public String getRevertidoPor() {
        return revertidoPor;
    }

    public void setRevertidoPor(String revertidoPor) {
        this.revertidoPor = revertidoPor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
