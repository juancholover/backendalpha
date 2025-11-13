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

@Entity
@Table(name = "semestres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditListener.class)
public class Semestre extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_semestre", unique = true, nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigoSemestre; // Ejemplo: 2025-I, 2025-II

    @Column(name = "nombre", nullable = false, length = 100)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String nombre;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "periodo", nullable = false)
    private Integer periodo; // 1 o 2 (I o II)

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "fecha_inicio_matricula")
    private LocalDate fechaInicioMatricula;

    @Column(name = "fecha_fin_matricula")
    private LocalDate fechaFinMatricula;

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // PLANIFICADO, EN_CURSO, FINALIZADO, CANCELADO

    @Column(name = "es_actual")
    private Boolean esActual = false;
}
