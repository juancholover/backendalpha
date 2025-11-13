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
@Table(name = "matricula")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class Matricula extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Column(name = "seccion", length = 10)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String seccion; // A, B, C

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula;

    @Column(name = "tipo_matricula", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoMatricula; // REGULAR, EXTRAORDINARIA

    @Column(name = "estado_matricula", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoMatricula; // MATRICULADO, RETIRADO, ANULADO

    @Column(name = "fecha_retiro")
    private LocalDate fechaRetiro;

    @Column(name = "nota_final")
    private Double notaFinal;

    @Column(name = "estado_aprobacion", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoAprobacion; // APROBADO, DESAPROBADO, RETIRADO, PENDIENTE

    @Column(name = "inasistencias")
    private Integer inasistencias = 0;
}
