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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cuenta_corriente_alumno")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class CuentaCorrienteAlumno extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto; // Monto de la deuda

    @Column(name = "monto_pagado", precision = 10, scale = 2)
    private BigDecimal montoPagado = BigDecimal.ZERO; // Monto ya pagado de esta deuda

    @Column(name = "monto_pendiente", precision = 10, scale = 2)
    private BigDecimal montoPendiente; // monto - montoPagado

    @Column(name = "concepto", nullable = false, length = 255)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String concepto; // Ej: "Matrícula 2025-I", "Pensión Marzo 2025"

    @Column(name = "tipo_cargo", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoCargo; // MATRICULA, PENSION, DERECHO_EXAMEN, CERTIFICADO, MORA, OTROS

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // PENDIENTE, PAGADO_PARCIAL, PAGADO, VENCIDO, ANULADO

    @Column(name = "periodo_academico", length = 20)
    private String periodoAcademico; // Ej: "2025-I" para asociar con el período

    @Column(name = "numero_cuota")
    private Integer numeroCuota; // Si es parte de un plan de cuotas

    @Column(name = "observaciones", length = 500)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String observaciones;

    // Relación inversa con pagos aplicados
    @OneToMany(mappedBy = "deuda", cascade = CascadeType.ALL)
    private Set<PagoDetalleDeuda> pagosDetalle = new HashSet<>();

    /**
     * Constructor de conveniencia
     */
    public CuentaCorrienteAlumno(Universidad universidad, Estudiante estudiante, 
                                 BigDecimal monto, String concepto, LocalDate fechaVencimiento) {
        this.universidad = universidad;
        this.estudiante = estudiante;
        this.monto = monto;
        this.concepto = concepto;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaEmision = LocalDate.now();
        this.montoPagado = BigDecimal.ZERO;
        this.montoPendiente = monto;
        this.estado = "PENDIENTE";
    }

    @PrePersist
    public void prePersist() {
        if (this.fechaEmision == null) {
            this.fechaEmision = LocalDate.now();
        }
        if (this.montoPagado == null) {
            this.montoPagado = BigDecimal.ZERO;
        }
        calcularMontoPendiente();
        actualizarEstado();
    }

    @PreUpdate
    public void preUpdate() {
        calcularMontoPendiente();
        actualizarEstado();
    }

    /**
     * Calcula el monto pendiente
     */
    private void calcularMontoPendiente() {
        if (monto != null && montoPagado != null) {
            this.montoPendiente = monto.subtract(montoPagado);
        }
    }

    /**
     * Actualiza el estado según el monto pagado
     */
    private void actualizarEstado() {
        if (montoPendiente == null) return;
        
        if (montoPendiente.compareTo(BigDecimal.ZERO) <= 0) {
            this.estado = "PAGADO";
        } else if (montoPagado.compareTo(BigDecimal.ZERO) > 0) {
            this.estado = "PAGADO_PARCIAL";
        } else if (fechaVencimiento != null && LocalDate.now().isAfter(fechaVencimiento)) {
            this.estado = "VENCIDO";
        } else {
            this.estado = "PENDIENTE";
        }
    }

    /**
     * Aplica un pago a esta deuda
     */
    public void aplicarPago(BigDecimal montoPago) {
        if (montoPago.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero");
        }
        
        BigDecimal nuevoMontoPagado = this.montoPagado.add(montoPago);
        if (nuevoMontoPagado.compareTo(this.monto) > 0) {
            throw new IllegalArgumentException("El pago excede el monto de la deuda");
        }
        
        this.montoPagado = nuevoMontoPagado;
        calcularMontoPendiente();
        actualizarEstado();
    }

    /**
     * Verifica si la deuda está completamente pagada
     */
    public boolean estaPagada() {
        return montoPendiente != null && montoPendiente.compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * Verifica si la deuda está vencida
     */
    public boolean estaVencida() {
        return fechaVencimiento != null && 
               LocalDate.now().isAfter(fechaVencimiento) && 
               !estaPagada();
    }
}
