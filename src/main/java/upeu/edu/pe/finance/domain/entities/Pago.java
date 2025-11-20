package upeu.edu.pe.finance.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"universidad", "estudiante", "pagosDetalle"})
@EntityListeners(AuditListener.class)
public class Pago extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(name = "numero_recibo", unique = true, nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String numeroRecibo; // Número de comprobante único

    @Column(name = "monto_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPagado; // Monto total recibido

    @Column(name = "monto_aplicado", precision = 10, scale = 2)
    private BigDecimal montoAplicado = BigDecimal.ZERO; // Monto ya aplicado a deudas

    @Column(name = "monto_pendiente_aplicar", precision = 10, scale = 2)
    private BigDecimal montoPendienteAplicar; // Saldo a favor del estudiante

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "metodo_pago", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String metodoPago; // EFECTIVO, TARJETA, TRANSFERENCIA, DEPOSITO, CHEQUE

    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago; // Número de operación, voucher, etc.

    @Column(name = "banco", length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String banco; // Banco donde se realizó el pago

    @Column(name = "cajero", length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String cajero; // Persona que recibió el pago

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // PENDIENTE_APLICAR, APLICADO, ANULADO

    @Column(name = "observaciones", length = 500)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String observaciones;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Column(name = "motivo_anulacion", length = 500)
    private String motivoAnulacion;

    // Relación inversa con los detalles de aplicación
    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PagoDetalleDeuda> pagosDetalle = new HashSet<>();

    /**
     * Constructor de conveniencia
     */
    public Pago(Universidad universidad, Estudiante estudiante, String numeroRecibo,
                       BigDecimal montoPagado, String metodoPago) {
        this.universidad = universidad;
        this.estudiante = estudiante;
        this.numeroRecibo = numeroRecibo;
        this.montoPagado = montoPagado;
        this.metodoPago = metodoPago;
        this.fechaPago = LocalDateTime.now();
        this.montoAplicado = BigDecimal.ZERO;
        this.montoPendienteAplicar = montoPagado;
        this.estado = "PENDIENTE_APLICAR";
    }

    @PrePersist
    public void prePersist() {
        if (this.fechaPago == null) {
            this.fechaPago = LocalDateTime.now();
        }
        if (this.montoAplicado == null) {
            this.montoAplicado = BigDecimal.ZERO;
        }
        calcularMontoPendienteAplicar();
        actualizarEstado();
    }

    @PreUpdate
    public void preUpdate() {
        calcularMontoPendienteAplicar();
        actualizarEstado();
    }

    /**
     * Calcula el monto pendiente de aplicar
     */
    private void calcularMontoPendienteAplicar() {
        if (montoPagado != null && montoAplicado != null) {
            this.montoPendienteAplicar = montoPagado.subtract(montoAplicado);
        }
    }

    /**
     * Actualiza el estado según el monto aplicado
     */
    private void actualizarEstado() {
        if (!"ANULADO".equals(this.estado)) {
            if (montoPendienteAplicar != null && montoPendienteAplicar.compareTo(BigDecimal.ZERO) <= 0) {
                this.estado = "APLICADO";
            } else {
                this.estado = "PENDIENTE_APLICAR";
            }
        }
    }

    /**
     * Aplica un monto de este pago a una deuda
     */
    public void aplicarMontoADeuda(BigDecimal montoAplicar) {
        if (montoAplicar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a aplicar debe ser mayor a cero");
        }
        
        if (montoAplicar.compareTo(this.montoPendienteAplicar) > 0) {
            throw new IllegalArgumentException("El monto a aplicar excede el saldo disponible del pago");
        }
        
        this.montoAplicado = this.montoAplicado.add(montoAplicar);
        calcularMontoPendienteAplicar();
        actualizarEstado();
    }

    /**
     * Anula el pago
     */
    public void anular(String motivo) {
        if ("ANULADO".equals(this.estado)) {
            throw new IllegalStateException("El pago ya está anulado");
        }
        
        if (this.montoAplicado.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("No se puede anular un pago que ya tiene aplicaciones");
        }
        
        this.estado = "ANULADO";
        this.fechaAnulacion = LocalDateTime.now();
        this.motivoAnulacion = motivo;
    }

    /**
     * Verifica si el pago tiene saldo disponible
     */
    public boolean tieneSaldoDisponible() {
        return montoPendienteAplicar != null && 
               montoPendienteAplicar.compareTo(BigDecimal.ZERO) > 0 &&
               !"ANULADO".equals(this.estado);
    }
}
