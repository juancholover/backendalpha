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
@Table(name = "estudiante", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo_estudiante", "universidad_id"}),
    @UniqueConstraint(columnNames = {"persona_id", "universidad_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Estudiante extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", nullable = false)
    private ProgramaAcademico programaAcademico;

    @Column(name = "codigo_estudiante", unique = true, nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigoEstudiante;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "ciclo_actual")
    private Integer cicloActual;

    @Column(name = "creditos_aprobados")
    private Integer creditosAprobados;

    @Column(name = "creditos_cursando")
    private Integer creditosCursando = 0; // Créditos matriculados en el ciclo actual

    @Column(name = "creditos_obligatorios_aprobados")
    private Integer creditosObligatoriosAprobados = 0;

    @Column(name = "creditos_electivos_aprobados")
    private Integer creditosElectivosAprobados = 0;

    @Column(name = "promedio_ponderado", precision = 5, scale = 2)
    private java.math.BigDecimal promedioPonderado;

    @Column(name = "modalidad_ingreso", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String modalidadIngreso; // EXAMEN, TRASLADO, CONVENIO

    @Column(name = "estado_academico", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoAcademico; // ACTIVO, RETIRADO, EGRESADO, SUSPENDIDO

    @Column(name = "tipo_estudiante", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoEstudiante; // REGULAR, IRREGULAR


    /**
     * Verifica si el estudiante puede graduarse según el plan académico
     */
    public boolean puedeGraduarse(PlanAcademico plan) {
        if (plan == null || creditosObligatoriosAprobados == null || creditosElectivosAprobados == null) {
            return false;
        }
        boolean cumpleObligatorios = creditosObligatoriosAprobados >= (plan.getCreditosObligatorios() != null ? plan.getCreditosObligatorios() : 0);
        boolean cumpleElectivos = creditosElectivosAprobados >= (plan.getCreditosElectivos() != null ? plan.getCreditosElectivos() : 0);
        return cumpleObligatorios && cumpleElectivos;
    }

    @PrePersist
    public void prePersist() {
        if (this.creditosCursando == null) {
            this.creditosCursando = 0;
        }
        if (this.creditosObligatoriosAprobados == null) {
            this.creditosObligatoriosAprobados = 0;
        }
        if (this.creditosElectivosAprobados == null) {
            this.creditosElectivosAprobados = 0;
        }
        if (this.estadoAcademico == null) {
            this.estadoAcademico = "ACTIVO";
        }
    }
}
