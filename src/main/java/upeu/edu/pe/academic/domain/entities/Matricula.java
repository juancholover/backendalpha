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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "matricula", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"estudiante_id", "curso_ofertado_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Matricula extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_ofertado_id", nullable = false)
    private CursoOfertado cursoOfertado;

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula = LocalDate.now();

    @Column(name = "tipo_matricula", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoMatricula = "REGULAR"; // REGULAR, EXTRAORDINARIA

    @Column(name = "creditos_matriculados")
    private Integer creditosMatriculados; // Créditos del curso matriculado

    @Column(name = "estado_matricula", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estadoMatricula = "MATRICULADO"; // MATRICULADO, RETIRADO, ANULADO

    @Column(name = "fecha_retiro")
    private LocalDate fechaRetiro;

    @OneToMany(mappedBy = "matricula", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EvaluacionNota> evaluacionNotas = new HashSet<>();


    public Matricula(Estudiante estudiante, CursoOfertado cursoOfertado) {
        this.estudiante = estudiante;
        this.cursoOfertado = cursoOfertado;
        this.fechaMatricula = LocalDate.now();
        this.estadoMatricula = "MATRICULADO";
        this.tipoMatricula = "REGULAR";
    }
    
    /**
     * Retira al estudiante del curso
     */
    public void retirar() {
        if ("RETIRADO".equals(this.estadoMatricula)) {
            throw new IllegalStateException("El estudiante ya está retirado");
        }
        this.estadoMatricula = "RETIRADO";
        this.fechaRetiro = LocalDate.now();
    }
}
