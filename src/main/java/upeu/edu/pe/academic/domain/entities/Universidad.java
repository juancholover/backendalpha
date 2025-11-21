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
@Table(name = "universidades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Universidad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 255)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;

    @Column(name = "dominio", unique = true, length = 50)
    @Normalize(Normalize.NormalizeType.LOWERCASE)
    private String dominio;

    @Column(name = "ruc", unique = true, nullable = false, length = 11)
    private String ruc; // RUC para Perú (identificación tributaria)

    @Column(name = "tipo", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipo; // PUBLICA, PRIVADA, CONSORCIO

    @Column(name = "website", length = 255)
    @Normalize(Normalize.NormalizeType.LOWERCASE)
    private String website; // URL oficial: https://upeu.edu.pe

    @Column(name = "logo_url", length = 500)
    private String logoUrl; // URL del logo institucional

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizacion_principal_id")
    private Localizacion localizacionPrincipal; // Sede principal (dirección/teléfono/email)

    @Column(name = "zona_horaria", length = 50)
    private String zonaHoraria;

    @Column(name = "locale", length = 20)
    private String locale; 

    @Column(name = "configuracion", columnDefinition = "jsonb") 
    private String configuracion;
}
