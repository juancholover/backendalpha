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
@Table(name = "estudiante")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class Estudiante extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_academico_id", nullable = false)
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

    @Column(name = "promedio_ponderado")
    private Double promedioPonderado;

    @Column(name = "modalidad_ingreso", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String modalidadIngreso; // EXAMEN, TRASLADO, CONVENIO

    @Column(name = "estado_academico", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoAcademico; // ACTIVO, RETIRADO, EGRESADO, SUSPENDIDO

    @Column(name = "tipo_estudiante", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoEstudiante; // REGULAR, IRREGULAR
}
