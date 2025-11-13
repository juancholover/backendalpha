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
@Table(name = "plan_academico")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class PlanAcademico extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_academico_id", nullable = false)
    private ProgramaAcademico programaAcademico;

    @Column(name = "codigo", unique = true, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo;

    @Column(name = "version", length = 10)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String version; // 2020, 2021, 2022, etc.

    @Column(name = "nombre", nullable = false, length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;

    @Column(name = "resolucion_aprobacion", length = 100)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String resolucionAprobacion;

    @Column(name = "fecha_aprobacion")
    private LocalDate fechaAprobacion;

    @Column(name = "fecha_vigencia_inicio")
    private LocalDate fechaVigenciaInicio;

    @Column(name = "fecha_vigencia_fin")
    private LocalDate fechaVigenciaFin;

    @Column(name = "creditos_totales")
    private Integer creditosTotales;

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // VIGENTE, OBSOLETO, EN_PROCESO
}
