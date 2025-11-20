package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

import java.time.LocalDate;

@Entity
@Table(name = "empleado", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo_empleado", "universidad_id"}),
    @UniqueConstraint(columnNames = {"persona_id", "universidad_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"persona", "universidad", "unidadOrganizativa"})
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
    private Universidad universidad; // Aislamiento multi-tenant

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_organizativa_id")
    private UnidadOrganizativa unidadOrganizativa;

    @Column(name = "codigo_empleado", unique = true, nullable = false, length = 20)
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

    @Column(name = "salario")
    private Double salario;

    @Column(name = "estado_laboral", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoLaboral; // ACTIVO, CESADO, SUSPENDIDO, LICENCIA
}
