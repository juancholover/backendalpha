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
@Table(name = "profesor", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"empleado_id", "universidad_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"empleado", "universidad"})
@EntityListeners(AuditListener.class)
public class Profesor extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false, unique = true)
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad; // Aislamiento multi-tenant

    @Column(name = "grado_academico", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String gradoAcademico; // BACHILLER, LICENCIADO, MAGISTER, DOCTOR

    @Column(name = "especialidad", length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String especialidad;

    @Column(name = "categoria_docente", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String categoriaDocente; // PRINCIPAL, ASOCIADO, AUXILIAR

    @Column(name = "condicion_docente", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String condicionDocente; // ORDINARIO, CONTRATADO

    @Column(name = "dedicacion", length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String dedicacion; // TIEMPO_COMPLETO, TIEMPO_PARCIAL

    @Column(name = "codigo_orcid", length = 50)
    private String codigoOrcid;

    @Column(name = "codigo_renacyt", length = 50)
    private String codigoRenacyt;
}
