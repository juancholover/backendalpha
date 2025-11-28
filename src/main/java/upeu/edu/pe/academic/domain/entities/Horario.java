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

@Entity
@Table(name = "horario", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "curso_ofertado_id", "dia_semana", "hora_inicio" })
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

    public static Horario crear(Universidad universidad, CursoOfertado cursoOfertado, Integer diaSemana,
            LocalTime horaInicio, LocalTime horaFin, Localizacion localizacion,
            String tipoSesion, String observaciones) {
        Horario horario = new Horario();
        horario.setUniversidad(universidad);
        horario.setCursoOfertado(cursoOfertado);
        horario.setDiaSemana(diaSemana);
        horario.setHoraInicio(horaInicio);
        horario.setHoraFin(horaFin);
        horario.setLocalizacion(localizacion);
        horario.setTipoSesion(tipoSesion);
        horario.setObservaciones(observaciones);
        return horario;
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
        if (diaSemana == null)
            return "";
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
