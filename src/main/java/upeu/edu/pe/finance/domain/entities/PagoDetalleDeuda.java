package upeu.edu.pe.finance.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago_detalle_deuda")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class PagoDetalleDeuda extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id", nullable = false)
    private Pago pago; // El pago recibido

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deuda_id", nullable = false)
    private CuentaCorrienteAlumno deuda; // La deuda a la que se aplica

    @Column(name = "monto_aplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoAplicado; // Monto de este pago que se aplica a esta deuda

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDateTime fechaAplicacion;

    @Column(name = "aplicado_por", length = 100)
    private String aplicadoPor; // Usuario que aplicó el pago

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "estado", length = 20)
    private String estado; // APLICADO, REVERTIDO

    @Column(name = "fecha_reversion")
    private LocalDateTime fechaReversion;

    @Column(name = "motivo_reversion", length = 500)
    private String motivoReversion;

    /**
     * Constructor de conveniencia
     */
    public PagoDetalleDeuda(Pago pago, CuentaCorrienteAlumno deuda, BigDecimal montoAplicado) {
        this.pago = pago;
        this.deuda = deuda;
        this.montoAplicado = montoAplicado;
        this.fechaAplicacion = LocalDateTime.now();
        this.estado = "APLICADO";
    }

    /**
     * Constructor completo
     */
    public PagoDetalleDeuda(Pago pago, CuentaCorrienteAlumno deuda, 
                           BigDecimal montoAplicado, String aplicadoPor) {
        this.pago = pago;
        this.deuda = deuda;
        this.montoAplicado = montoAplicado;
        this.aplicadoPor = aplicadoPor;
        this.fechaAplicacion = LocalDateTime.now();
        this.estado = "APLICADO";
    }

    @PrePersist
    public void prePersist() {
        if (this.fechaAplicacion == null) {
            this.fechaAplicacion = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = "APLICADO";
        }
        validarAplicacion();
    }

    /**
     * Validaciones de negocio antes de persistir
     */
    private void validarAplicacion() {
        // Validar que el monto sea positivo
        if (montoAplicado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto aplicado debe ser mayor a cero");
        }

        // Validar que el pago tenga saldo suficiente
        if (pago != null && !pago.tieneSaldoDisponible()) {
            throw new IllegalStateException("El pago no tiene saldo disponible");
        }

        // Validar que la deuda no esté completamente pagada
        if (deuda != null && deuda.estaPagada()) {
            throw new IllegalStateException("La deuda ya está completamente pagada");
        }

        // Validar que el monto no exceda la deuda pendiente
        if (deuda != null && montoAplicado.compareTo(deuda.getMontoPendiente()) > 0) {
            throw new IllegalArgumentException("El monto aplicado excede el monto pendiente de la deuda");
        }
    }

    /**
     * Aplica este detalle de pago
     * Actualiza tanto el pago como la deuda
     */
    @PostPersist
    public void aplicar() {
        if (pago != null && deuda != null && "APLICADO".equals(estado)) {
            pago.aplicarMontoADeuda(montoAplicado);
            deuda.aplicarPago(montoAplicado);
        }
    }

    /**
     * Revierte esta aplicación de pago
     */
    public void revertir(String motivo) {
        if ("REVERTIDO".equals(this.estado)) {
            throw new IllegalStateException("Esta aplicación ya fue revertida");
        }

        this.estado = "REVERTIDO";
        this.fechaReversion = LocalDateTime.now();
        this.motivoReversion = motivo;

        // Revertir en el pago
        if (pago != null) {
            BigDecimal nuevoMontoAplicado = pago.getMontoAplicado().subtract(montoAplicado);
            pago.setMontoAplicado(nuevoMontoAplicado);
        }

        // Revertir en la deuda
        if (deuda != null) {
            BigDecimal nuevoMontoPagado = deuda.getMontoPagado().subtract(montoAplicado);
            deuda.setMontoPagado(nuevoMontoPagado);
        }
    }

    /**
     * Verifica si esta aplicación está activa
     */
    public boolean estaActiva() {
        return "APLICADO".equals(this.estado);
    }
}
