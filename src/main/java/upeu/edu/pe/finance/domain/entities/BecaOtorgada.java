package upeu.edu.pe.finance.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.people.domain.entities.Estudiante;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que registra las becas otorgadas a estudiantes por promedio de excelencia.
 * Almacena auditoría de becas automáticas aplicadas cuando el promedio >= 16.00
 */
@Entity
@Table(name = "beca_otorgada")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class BecaOtorgada extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_corriente_id", nullable = false)
    private CuentaCorrienteAlumno cuentaCorriente;

    @Column(name = "tipo_beca", length = 50, nullable = false)
    private String tipoBeca; // EXCELENCIA_ACADEMICA, DEPORTIVA, SOCIOECONOMICA, etc.

    @Column(name = "promedio_alcanzado", precision = 5, scale = 2, nullable = false)
    private BigDecimal promedioAlcanzado;

    @Column(name = "porcentaje_beca", precision = 5, scale = 2, nullable = false)
    private BigDecimal porcentajeBeca; // 50.00 para 50%

    @Column(name = "monto_beneficio", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoBeneficio; // Monto descontado

    @Column(name = "saldo_anterior", precision = 10, scale = 2, nullable = false)
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_nuevo", precision = 10, scale = 2, nullable = false)
    private BigDecimal saldoNuevo;

    @Column(name = "fecha_otorgamiento", nullable = false)
    private LocalDate fechaOtorgamiento;

    @Column(name = "periodo_academico", length = 50)
    private String periodoAcademico; // Período en el que se otorgó

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado; // APLICADA, REVOCADA, OBSERVADA
}
