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
@Table(name = "programa_academico")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class ProgramaAcademico extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_organizativa_id", nullable = false)
    private UnidadOrganizativa unidadOrganizativa;

    @Column(name = "nombre", nullable = false, length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;

    @Column(name = "codigo", unique = true, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo;

    @Column(name = "nivel_academico", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String nivelAcademico; // PREGRADO, POSGRADO, MAESTRIA, DOCTORADO

    @Column(name = "modalidad", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String modalidad; // PRESENCIAL, SEMIPRESENCIAL, A_DISTANCIA

    @Column(name = "duracion_semestres")
    private Integer duracionSemestres;

    @Column(name = "creditos_totales")
    private Integer creditosTotales;

    @Column(name = "titulo_otorgado", length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String tituloOtorgado;

    @Column(name = "grado_otorgado", length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String gradoOtorgado;

    @Column(name = "resolucion_creacion", length = 100)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String resolucionCreacion;

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // ACTIVO, INACTIVO, PROCESO_CIERRE
}
