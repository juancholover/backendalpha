package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;
import java.time.LocalTime;

/**
 * Entidad que representa los bloques horarios de un curso ofertado.
 * Permite definir múltiples sesiones por semana (teoría, práctica, laboratorio).
 * 
 * Ejemplo:
 * - Programación I - Sección A
 *   - Lunes 08:00-10:00 (Teoría, Aula A-101)
 *   - Miércoles 08:00-10:00 (Teoría, Aula A-101)
 *   - Viernes 14:00-18:00 (Laboratorio, Lab B-201)
 */
@Entity
@Table(name = "horario", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"curso_ofertado_id", "dia_semana", "hora_inicio"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Horario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_id", nullable = false)
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_ofertado_id", nullable = false)
    private CursoOfertado cursoOfertado;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana; // 1=Lunes, 2=Martes, 3=Miércoles, 4=Jueves, 5=Viernes, 6=Sábado, 7=Domingo

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizacion_id")
    private Localizacion localizacion; // Puede ser diferente por sesión (teoría en aula, práctica en lab)

    @Column(name = "tipo_sesion", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipoSesion; // TEORIA, PRACTICA, LABORATORIO, TALLER, SEMINARIO

    @Column(name = "observaciones", length = 500)
    @Normalize(Normalize.NormalizeType.SPACES_ONLY)
    private String observaciones;

    /**
     * Valida que la hora de inicio sea anterior a la hora de fin
     */
    @PrePersist
    @PreUpdate
    private void validarHorarios() {
        if (horaInicio != null && horaFin != null) {
            if (!horaInicio.isBefore(horaFin)) {
                throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
            }
        }
        
        if (diaSemana != null && (diaSemana < 1 || diaSemana > 7)) {
            throw new IllegalArgumentException("El día de la semana debe estar entre 1 (Lunes) y 7 (Domingo)");
        }
    }

    /**
     * Verifica si este horario se cruza con otro
     */
    public boolean seCruzaCon(Horario otro) {
        // Mismo día de la semana
        if (!this.diaSemana.equals(otro.diaSemana)) {
            return false;
        }
        
        // Verificar cruce de horas
        return !(this.horaFin.isBefore(otro.horaInicio) || this.horaInicio.isAfter(otro.horaFin));
    }

    /**
     * Obtiene la duración en minutos del bloque horario
     */
    public int getDuracionMinutos() {
        if (horaInicio != null && horaFin != null) {
            return (int) java.time.Duration.between(horaInicio, horaFin).toMinutes();
        }
        return 0;
    }

    /**
     * Obtiene el nombre del día de la semana
     */
    public String getNombreDia() {
        if (diaSemana == null) return "";
        return switch (diaSemana) {
            case 1 -> "Lunes";
            case 2 -> "Martes";
            case 3 -> "Miércoles";
            case 4 -> "Jueves";
            case 5 -> "Viernes";
            case 6 -> "Sábado";
            case 7 -> "Domingo";
            default -> "Desconocido";
        };
    }
}
