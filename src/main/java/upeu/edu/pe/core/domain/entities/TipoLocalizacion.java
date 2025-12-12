package upeu.edu.pe.core.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

@Entity
@Table(name = "tipo_localizacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class TipoLocalizacion extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo; // Ej: SEDE, EDIFICIO, PISO, AULA, LAB

    @Column(name = "nombre", nullable = false, length = 100)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre; // Ej: Sede, Edificio, Piso, Aula, Laboratorio

    // Relación recursiva para jerarquías de tipos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_padre_id")
    private TipoLocalizacion padre; // Ej: Aula tiene padre=Piso, Piso tiene padre=Edificio

    @Column(name = "nivel_jerarquia", nullable = false)
    private Integer nivelJerarquia; // 1=Sede, 2=Edificio, 3=Piso, 4=Aula

    @Column(name = "permite_asignacion", nullable = false)
    private Boolean permiteAsignacion = false; // TRUE si se pueden asignar actividades (ej: Aula=true, Edificio=false)
}
