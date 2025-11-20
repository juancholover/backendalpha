package upeu.edu.pe.finance.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class PagoDetalleDeudaRequestDTO {

    @NotNull(message = "El ID del pago es obligatorio")
    private Long pagoId;

    @NotNull(message = "El ID de la deuda es obligatorio")
    private Long deudaId;

    @NotNull(message = "El monto aplicado es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal montoAplicado;

    @NotNull(message = "El usuario que aplica es obligatorio")
    private String aplicadoPor;

    // Constructors
    public PagoDetalleDeudaRequestDTO() {
    }

    public PagoDetalleDeudaRequestDTO(Long pagoId, Long deudaId, BigDecimal montoAplicado, String aplicadoPor) {
        this.pagoId = pagoId;
        this.deudaId = deudaId;
        this.montoAplicado = montoAplicado;
        this.aplicadoPor = aplicadoPor;
    }

    // Getters and Setters
    public Long getPagoId() {
        return pagoId;
    }

    public void setPagoId(Long pagoId) {
        this.pagoId = pagoId;
    }

    public Long getDeudaId() {
        return deudaId;
    }

    public void setDeudaId(Long deudaId) {
        this.deudaId = deudaId;
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
}
