package upeu.edu.pe.assessment.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.assessment.application.dto.AsistenciaAlumnoResponseDTO;
import upeu.edu.pe.assessment.application.mapper.AsistenciaAlumnoMapper;
import upeu.edu.pe.assessment.domain.entities.AsistenciaAlumno;
import upeu.edu.pe.assessment.domain.repositories.AsistenciaAlumnoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de dominio para consultas básicas de asistencia de estudiantes.
 * 
 * Este servicio solo contiene operaciones de LECTURA y cálculos estadísticos.
 * Las operaciones de ESCRITURA (registrar asistencia) se manejan en Use Cases.
 * 
 * @author Sistema UPeU
 */
@ApplicationScoped
public class AsistenciaAlumnoService {

    @Inject
    AsistenciaAlumnoRepository asistenciaRepository;

    @Inject
    AsistenciaAlumnoMapper asistenciaMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    /**
     * Busca asistencia por ID.
     */
    public AsistenciaAlumnoResponseDTO findById(Long id) {
        AsistenciaAlumno asistencia = asistenciaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Registro de asistencia no encontrado con ID: " + id));
        return asistenciaMapper.toResponseDTO(asistencia);
    }

    /**
     * Busca asistencias por estudiante y sección.
     */
    public List<AsistenciaAlumnoResponseDTO> findByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        List<AsistenciaAlumno> asistencias = asistenciaRepository.findByEstudianteAndSeccion(estudianteId, seccionId);
        return asistenciaMapper.toResponseDTOList(asistencias);
    }

    /**
     * Busca asistencias por horario y fecha.
     */
    public List<AsistenciaAlumnoResponseDTO> findByHorarioAndFecha(Long horarioId, LocalDate fecha) {
        List<AsistenciaAlumno> asistencias = asistenciaRepository.findByHorarioAndFecha(horarioId, fecha);
        return asistenciaMapper.toResponseDTOList(asistencias);
    }

    /**
     * Busca asistencias por sección y rango de fechas.
     */
    public List<AsistenciaAlumnoResponseDTO> findBySeccionAndFechaRange(Long seccionId, LocalDate fechaInicio,
            LocalDate fechaFin) {
        List<AsistenciaAlumno> asistencias = asistenciaRepository.findBySeccionAndFechaRange(seccionId, fechaInicio,
                fechaFin);
        return asistenciaMapper.toResponseDTOList(asistencias);
    }

    /**
     * Busca ausencias de un estudiante en una sección.
     */
    public List<AsistenciaAlumnoResponseDTO> findAusenciasByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        List<AsistenciaAlumno> asistencias = asistenciaRepository.findAusenciasByEstudianteAndSeccion(estudianteId,
                seccionId);
        return asistenciaMapper.toResponseDTOList(asistencias);
    }

    // =====================================================
    // OPERACIONES DE ESTADÍSTICAS
    // =====================================================

    /**
     * Calcula el porcentaje de asistencia de un estudiante en una sección.
     * 
     * @return Porcentaje de asistencia (0-100)
     */
    public BigDecimal calcularPorcentajeAsistencia(Long estudianteId, Long seccionId) {
        long totalClases = asistenciaRepository.countByEstudianteAndSeccion(estudianteId, seccionId);

        if (totalClases == 0) {
            return BigDecimal.ZERO;
        }

        long asistencias = asistenciaRepository.countAsistenciasByEstudianteAndSeccion(estudianteId, seccionId);

        return new BigDecimal(asistencias * 100)
                .divide(new BigDecimal(totalClases), 2, RoundingMode.HALF_UP);
    }

    /**
     * Verifica si el estudiante está en riesgo de inhabilitación.
     * Regla institucional: menos de 70% de asistencia = riesgo
     */
    public boolean estaEnRiesgoInhabilitacion(Long estudianteId, Long seccionId) {
        BigDecimal porcentaje = calcularPorcentajeAsistencia(estudianteId, seccionId);
        return porcentaje.compareTo(new BigDecimal("70")) < 0;
    }

    /**
     * Cuenta el total de clases de una sección.
     */
    public long countClasesBySeccion(Long seccionId) {
        return asistenciaRepository.countClasesBySeccion(seccionId);
    }

    /**
     * Cuenta las ausencias de un estudiante en una sección.
     */
    public long countAusenciasByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        return asistenciaRepository.countAusenciasByEstudianteAndSeccion(estudianteId, seccionId);
    }

    /**
     * Resultado del resumen de asistencia.
     */
    public record ResumenAsistencia(
            long totalClases,
            long asistencias,
            long ausencias,
            long tardanzas,
            long justificadas,
            BigDecimal porcentajeAsistencia,
            boolean enRiesgo) {
    }

    /**
     * Obtiene un resumen completo de asistencia de un estudiante.
     */
    public ResumenAsistencia getResumenAsistencia(Long estudianteId, Long seccionId) {
        long totalClases = asistenciaRepository.countByEstudianteAndSeccion(estudianteId, seccionId);
        long asistencias = asistenciaRepository.countAsistenciasByEstudianteAndSeccion(estudianteId, seccionId);
        long ausencias = asistenciaRepository.countAusenciasByEstudianteAndSeccion(estudianteId, seccionId);
        long tardanzas = asistenciaRepository.countTardanzasByEstudianteAndSeccion(estudianteId, seccionId);
        long justificadas = asistenciaRepository.countJustificadasByEstudianteAndSeccion(estudianteId, seccionId);

        BigDecimal porcentaje = calcularPorcentajeAsistencia(estudianteId, seccionId);
        boolean enRiesgo = porcentaje.compareTo(new BigDecimal("70")) < 0;

        return new ResumenAsistencia(
                totalClases,
                asistencias,
                ausencias,
                tardanzas,
                justificadas,
                porcentaje,
                enRiesgo);
    }
}
