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
@Table(name = "plan_academico", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class PlanAcademico extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_academico_id", nullable = false)
    private ProgramaAcademico programaAcademico;

    @Column(name = "codigo", nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo;

    @Column(name = "version", length = 10)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String version; // 2020, 2021, 2022, etc.

    @Column(name = "nombre", nullable = false, length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;


    @Column(name = "fecha_aprobacion")
    private LocalDate fechaAprobacion;

    @Column(name = "fecha_vigencia_inicio")
    private LocalDate fechaVigenciaInicio;

    @Column(name = "fecha_vigencia_fin")
    private LocalDate fechaVigenciaFin;

    @Column(name = "creditos_totales")
    private Integer creditosTotales;


    @Column(name = "creditos_obligatorios")
    private Integer creditosObligatorios; // Créditos de cursos obligatorios

    @Column(name = "creditos_electivos")
    private Integer creditosElectivos; // Créditos de cursos electivos

    @Column(name = "duracion_semestres")
    private Integer duracionSemestres; // Duración del plan en semestres

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // VIGENTE, OBSOLETO, EN_PROCESO


    @Column(name = "creditos_maximos_por_ciclo")
    private Integer creditosMaximosPorCiclo; // Máximo de créditos que puede matricular por ciclo (ej: 22)

    @Column(name = "creditos_minimos_tiempo_completo")
    private Integer creditosMinimosTiempoCompleto; // Mínimo para ser considerado tiempo completo (ej: 12)

    @Column(name = "duracion_ciclo_meses")
    private Integer duracionCicloMeses; // Duración del ciclo en meses (4=cuatrimestral, 6=semestral)

    /**
     * Valida que la distribución de créditos sea correcta
     */
    public boolean validarCreditos() {
        if (creditosTotales == null || creditosObligatorios == null || creditosElectivos == null) {
            return false;
        }
        return (creditosObligatorios + creditosElectivos) == creditosTotales;
    }
}
