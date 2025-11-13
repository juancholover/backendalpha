package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

@Entity
@Table(name = "curso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class Curso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_academico_id", nullable = false)
    private PlanAcademico planAcademico;

    @Column(name = "codigo_curso", unique = true, nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigoCurso;

    @Column(name = "nombre", nullable = false, length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String descripcion;

    @Column(name = "creditos", nullable = false)
    private Integer creditos;

    @Column(name = "horas_teoricas")
    private Integer horasTeoricas;

    @Column(name = "horas_practicas")
    private Integer horasPracticas;

    @Column(name = "horas_semanales")
    private Integer horasSemanales;

    @Column(name = "ciclo", nullable = false)
    private Integer ciclo; // Semestre/Ciclo en el que se dicta

    @Column(name = "tipo_curso", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoCurso; // OBLIGATORIO, ELECTIVO, LIBRE

    @Column(name = "area_curricular", length = 100)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String areaCurricular; // FORMACION_GENERAL, FORMACION_PROFESIONAL, ESPECIALIDAD

    @Column(name = "silabo_url", length = 255)
    private String silaboUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisito_id")
    private Curso prerequisito; // Curso prerequisito
}
