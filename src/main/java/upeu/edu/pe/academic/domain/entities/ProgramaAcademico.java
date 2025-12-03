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
@Table(name = "programa_academico", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigo"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class ProgramaAcademico extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_organizativa_id", nullable = false)
    private UnidadOrganizativa unidadOrganizativa;

    @Column(name = "nombre", nullable = false, length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;

    @Column(name = "codigo", nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo;

    @Column(name = "nivel_academico", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String nivelAcademico; // PREGRADO, SEGUNDA_ESPECIALIDAD, MAESTRIA, DOCTORADO, DIPLOMADO

    @Column(name = "modalidad", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String modalidad; // PRESENCIAL, SEMIPRESENCIAL, A_DISTANCIA

    @Column(name = "duracion_anios")
    private Integer duracionAnios; // Duración nominal en años

    @Column(name = "duracion_semestres")
    private Integer duracionSemestres;

    @Column(name = "creditos_totales")
    private Integer creditosTotales;

    @Column(name = "titulo_otorgado", length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String tituloOtorgado;

    @Column(name = "grado_academico", length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String gradoAcademico; // Bachiller, Magíster, Doctor

    @Column(name = "cupo_maximo_anual")
    private Integer cupoMaximoAnual; // Vacantes totales por año

    @Column(name = "nota_minima_ingreso", precision = 4, scale = 2)
    private BigDecimal notaMinimaIngreso; // Nota mínima en examen de admisión

    @Column(name = "programa_padre_id")
    private Long programaPadreId; // Para jerarquía (Maestría → Pregrado)

    @Column(name = "fecha_creacion_programa")
    private LocalDate fechaCreacionPrograma; // Fecha de creación oficial

    @Column(name = "fecha_ultima_modificacion_plan")
    private LocalDate fechaUltimaModificacionPlan; // Última actualización del plan

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // ACTIVO, EN_PROCESO_CIERRE, CERRADO, EN_APROBACION
}
