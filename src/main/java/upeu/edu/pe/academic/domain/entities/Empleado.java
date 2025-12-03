package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "empleado", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo_empleado"}),
    @UniqueConstraint(columnNames = {"persona_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Empleado extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_organizativa_id")
    private UnidadOrganizativa unidadOrganizativa;

    @Column(name = "codigo_empleado", nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigoEmpleado;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_cese")
    private LocalDate fechaCese;

    @Column(name = "cargo", length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String cargo;

    @Column(name = "tipo_contrato", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoContrato; // NOMBRADO, CONTRATADO, TEMPORAL

    @Column(name = "regimen_laboral", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String regimenLaboral;

    // IMPORTANTE: BigDecimal para dinero (precisión decimal exacta)
    @Column(name = "salario", precision = 10, scale = 2)
    private BigDecimal salario;

    @Column(name = "estado_laboral", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoLaboral; // ACTIVO, CESADO, SUSPENDIDO, LICENCIA
}
