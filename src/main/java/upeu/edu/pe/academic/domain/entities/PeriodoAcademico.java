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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "periodo_academico", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo_periodo", "universidad_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class PeriodoAcademico extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @Column(name = "codigo_periodo", nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigoPeriodo; // Ejemplo: 2025-I, 2025-II, 2025-VERANO

    @Column(name = "nombre", nullable = false, length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre; // "Primer Semestre 2025", "Semestre Académico 2025-I"

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "tipo_periodo", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoPeriodo; // SEMESTRE, TRIMESTRE, CUATRIMESTRE, BIMESTRE, ANUAL, VERANO

    @Column(name = "numero_periodo")
    private Integer numeroPeriodo; // 1, 2, 3... según el tipo

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "fecha_inicio_matricula")
    private LocalDate fechaInicioMatricula;

    @Column(name = "fecha_fin_matricula")
    private LocalDate fechaFinMatricula;

    @Column(name = "fecha_inicio_clases")
    private LocalDate fechaInicioClases;

    @Column(name = "fecha_fin_clases")
    private LocalDate fechaFinClases;

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // PLANIFICADO, MATRICULA_ABIERTA, EN_CURSO, FINALIZADO, CANCELADO

    @Column(name = "es_actual")
    private Boolean esActual = false;

    @Column(name = "descripcion", length = 500)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String descripcion;

    @OneToMany(mappedBy = "periodoAcademico", fetch = FetchType.LAZY)
    private List<CursoOfertado> cursosOfertados = new ArrayList<>();

    /**
     * Constructor de conveniencia
     */
    public PeriodoAcademico(Universidad universidad, String codigoPeriodo, String nombre, 
                           LocalDate fechaInicio, LocalDate fechaFin) {
        this.universidad = universidad;
        this.codigoPeriodo = codigoPeriodo;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = "PLANIFICADO";
        this.esActual = false;
    }

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = "PLANIFICADO";
        }
        if (this.esActual == null) {
            this.esActual = false;
        }
    }
}
