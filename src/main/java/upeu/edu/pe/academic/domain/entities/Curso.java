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
@Table(name = "curso", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "codigo_curso", "universidad_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Curso extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @Column(name = "codigo_curso", nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigoCurso;

    @Column(name = "nombre", nullable = false, length = 200)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String descripcion;

    @Column(name = "horas_teoricas")
    private Integer horasTeoricas;

    @Column(name = "horas_practicas")
    private Integer horasPracticas;

    @Column(name = "horas_semanales")
    private Integer horasSemanales;

    @Column(name = "tipo_curso", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoCurso; // OBLIGATORIO, ELECTIVO, LIBRE

    @Column(name = "area_curricular", length = 100)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String areaCurricular; // FORMACION_GENERAL, FORMACION_PROFESIONAL, ESPECIALIDAD

    @Column(name = "silabo_url", length = 255)
    private String silaboUrl;

    public static Curso crear(Universidad universidad, String codigoCurso, String nombre,
            String descripcion, Integer horasTeoricas, Integer horasPracticas,
            Integer horasSemanales, String tipoCurso, String areaCurricular,
            String silaboUrl) {
        Curso curso = new Curso();
        curso.setUniversidad(universidad);
        curso.setCodigoCurso(codigoCurso);
        curso.setNombre(nombre);
        curso.setDescripcion(descripcion);

        // Default values
        curso.setHorasTeoricas(horasTeoricas != null ? horasTeoricas : 0);
        curso.setHorasPracticas(horasPracticas != null ? horasPracticas : 0);

        if (horasSemanales != null) {
            curso.setHorasSemanales(horasSemanales);
        } else {
            curso.setHorasSemanales(curso.getHorasTeoricas() + curso.getHorasPracticas());
        }

        curso.setTipoCurso(tipoCurso != null ? tipoCurso : "OBLIGATORIO");
        curso.setAreaCurricular(areaCurricular);
        curso.setSilaboUrl(silaboUrl);

        return curso;
    }
}
