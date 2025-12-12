package upeu.edu.pe.curriculum.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.enrollment.domain.entities.Localizacion;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;

import java.time.LocalDate;

/**
 * Representa la publicación de una plantilla de sílabo en un campus específico.
 * 
 * Cada registro es una COPIA del sílabo plantilla adaptada a un campus.
 * El campus puede modificar fechas/evaluaciones según el nivel de flexibilidad.
 * 
 * Ejemplo:
 * - Plantilla: Silabo "Programación I 2025 v1"
 * - Publicaciones:
 *   1. Campus Lima (inicio: 01/03/2025)
 *   2. Campus Arequipa (inicio: 08/03/2025)
 *   3. Campus Juliaca (inicio: 15/03/2025)
 */
@Entity
@Table(name = "silabo_publicacion", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"plantilla_id", "localizacion_id", "anio_academico"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class SilaboPublicacion extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Plantilla origen.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantilla_id", nullable = false)
    private SilaboPlantilla plantilla;

    /**
     * Campus/Filial donde se publica.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizacion_id", nullable = false)
    private Localizacion localizacion;

    /**
     * Sílabo COPIA creado para este campus.
     * Este es el sílabo que el campus puede modificar (según restricciones).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "silabo_campus_id")
    private Silabo silaboCampus;

    /**
     * Año académico para el cual se publica.
     */
    @Column(name = "anio_academico", nullable = false, length = 10)
    private String anioAcademico;

    /**
     * Estado de la publicación:
     * - PENDIENTE: Publicación programada, aún no activa
     * - ACTIVA: Vigente en el campus
     * - FINALIZADA: Ciclo académico terminado
     * - CANCELADA: Publicación cancelada
     */
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, ACTIVA, FINALIZADA, CANCELADA

    /**
     * Fecha en que se publicó al campus.
     */
    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    /**
     * Usuario que realizó la publicación (sede_central).
     */
    @Column(name = "publicado_por", length = 200)
    private String publicadoPor;

    /**
     * Fecha de inicio del semestre en el campus.
     * Los campus pueden tener calendarios distintos.
     */
    @Column(name = "fecha_inicio_campus")
    private LocalDate fechaInicioCampus;

    /**
     * Fecha de fin del semestre en el campus.
     */
    @Column(name = "fecha_fin_campus")
    private LocalDate fechaFinCampus;

    /**
     * Indica si el campus ya realizó adaptaciones.
     */
    @Column(name = "adaptado", nullable = false)
    private Boolean adaptado = false;

    /**
     * Usuario del campus que realizó adaptaciones (campus_admin).
     */
    @Column(name = "adaptado_por", length = 200)
    private String adaptadoPor;

    /**
     * Fecha de última adaptación.
     */
    @Column(name = "fecha_adaptacion")
    private LocalDate fechaAdaptacion;

    /**
     * Notas sobre las adaptaciones realizadas.
     */
    @Column(name = "notas_adaptacion", columnDefinition = "TEXT")
    private String notasAdaptacion;

    /**
     * Activa la publicación.
     */
    public void activar(String usuario) {
        this.estado = "ACTIVA";
        this.fechaPublicacion = LocalDate.now();
        this.publicadoPor = usuario;
    }

    /**
     * Marca como adaptado por el campus.
     */
    public void marcarAdaptado(String usuario, String notas) {
        this.adaptado = true;
        this.adaptadoPor = usuario;
        this.fechaAdaptacion = LocalDate.now();
        this.notasAdaptacion = notas;
    }

    /**
     * Finaliza la publicación (fin de ciclo).
     */
    public void finalizar() {
        this.estado = "FINALIZADA";
    }

    /**
     * Cancela la publicación.
     */
    public void cancelar() {
        this.estado = "CANCELADA";
    }

    /**
     * Verifica si la publicación está activa.
     */
    public boolean estaActiva() {
        return "ACTIVA".equals(estado);
    }
}
