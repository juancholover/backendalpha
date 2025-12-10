package upeu.edu.pe.assessment.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.assessment.application.dto.AsistenciaAlumnoRequestDTO;
import upeu.edu.pe.assessment.application.dto.AsistenciaAlumnoResponseDTO;
import upeu.edu.pe.assessment.application.mapper.AsistenciaAlumnoMapper;
import upeu.edu.pe.assessment.domain.entities.AsistenciaAlumno;
import upeu.edu.pe.enrollment.domain.entities.Horario;
import upeu.edu.pe.assessment.domain.repositories.AsistenciaAlumnoRepository;
import upeu.edu.pe.people.domain.repositories.EstudianteRepository;
import upeu.edu.pe.enrollment.domain.repositories.HorarioRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@ApplicationScoped
public class AsistenciaAlumnoService {

    @Inject
    AsistenciaAlumnoRepository asistenciaRepository;

    @Inject
    EstudianteRepository estudianteRepository;

    @Inject
    HorarioRepository horarioRepository;

    @Inject
    AsistenciaAlumnoMapper asistenciaMapper;

    public List<AsistenciaAlumnoResponseDTO> findAll() {
        return asistenciaMapper.toResponseDTOList(asistenciaRepository.findAllActive());
    }

    public AsistenciaAlumnoResponseDTO findById(Long id) {
        AsistenciaAlumno asistencia = asistenciaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Asistencia no encontrada con ID: " + id));
        return asistenciaMapper.toResponseDTO(asistencia);
    }

    public List<AsistenciaAlumnoResponseDTO> findByEstudiante(Long estudianteId) {
        return asistenciaMapper.toResponseDTOList(asistenciaRepository.findByEstudiante(estudianteId));
    }

    public List<AsistenciaAlumnoResponseDTO> findByEstudianteAndFechaRange(Long estudianteId, LocalDate fechaInicio, LocalDate fechaFin) {
        return asistenciaMapper.toResponseDTOList(
                asistenciaRepository.findByEstudianteAndFechaRange(estudianteId, fechaInicio, fechaFin));
    }

    public List<AsistenciaAlumnoResponseDTO> findByHorarioAndFecha(Long horarioId, LocalDate fechaClase) {
        return asistenciaMapper.toResponseDTOList(
                asistenciaRepository.findByHorarioAndFecha(horarioId, fechaClase));
    }

    public List<AsistenciaAlumnoResponseDTO> findByEstudianteAndFecha(Long estudianteId, LocalDate fechaClase) {
        return asistenciaMapper.toResponseDTOList(
                asistenciaRepository.findByEstudianteAndFecha(estudianteId, fechaClase));
    }

    public Map<String, Object> getEstadisticasAsistencia(Long estudianteId, Long horarioId) {
        long totalClases = asistenciaRepository.countByEstudianteAndHorario(estudianteId, horarioId);
        long presentes = asistenciaRepository.countByEstudianteAndEstado(estudianteId, "PRESENTE");
        long ausentes = asistenciaRepository.countByEstudianteAndEstado(estudianteId, "AUSENTE");
        long tardanzas = asistenciaRepository.countByEstudianteAndEstado(estudianteId, "TARDANZA");
        long justificados = asistenciaRepository.countByEstudianteAndEstado(estudianteId, "JUSTIFICADO");
        double porcentaje = asistenciaRepository.calcularPorcentajeAsistencia(estudianteId, horarioId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalClases", totalClases);
        stats.put("presentes", presentes);
        stats.put("ausentes", ausentes);
        stats.put("tardanzas", tardanzas);
        stats.put("justificados", justificados);
        stats.put("porcentajeAsistencia", porcentaje);

        return stats;
    }

    @Transactional
    public AsistenciaAlumnoResponseDTO create(AsistenciaAlumnoRequestDTO requestDTO) {
        // Validar que el estudiante existe
        estudianteRepository.findByIdOptional(requestDTO.getEstudianteId())
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado con ID: " + requestDTO.getEstudianteId()));

        // Validar que el horario existe
        Horario horario = horarioRepository.findByIdOptional(requestDTO.getHorarioId())
                .orElseThrow(() -> new NotFoundException("Horario no encontrado con ID: " + requestDTO.getHorarioId()));

        // Validar que no existe ya un registro de asistencia para este estudiante, horario y fecha
        if (asistenciaRepository.existsByEstudianteHorarioFecha(
                requestDTO.getEstudianteId(), 
                requestDTO.getHorarioId(), 
                requestDTO.getFechaClase())) {
            throw new BusinessException("Ya existe un registro de asistencia para este estudiante en esta fecha y horario");
        }

        // Validar que la fecha coincide con el día de la semana del horario
        int diaSemanaFecha = requestDTO.getFechaClase().getDayOfWeek().getValue(); // 1=Lunes, 7=Domingo
        if (!horario.getDiaSemana().equals(diaSemanaFecha)) {
            throw new BusinessException("La fecha de clase no coincide con el día de la semana del horario. " +
                    "Horario es para " + horario.getNombreDia() + " pero la fecha es " + 
                    requestDTO.getFechaClase().getDayOfWeek());
        }

        // Validar que si el estado es TARDANZA, debe tener minutos de tardanza
        if ("TARDANZA".equals(requestDTO.getEstado()) && 
            (requestDTO.getMinutosTardanza() == null || requestDTO.getMinutosTardanza() <= 0)) {
            throw new BusinessException("Debe especificar los minutos de tardanza cuando el estado es TARDANZA");
        }

        AsistenciaAlumno asistencia = asistenciaMapper.toEntity(requestDTO);
        asistenciaRepository.persist(asistencia);
        return asistenciaMapper.toResponseDTO(asistencia);
    }

    @Transactional
    public AsistenciaAlumnoResponseDTO update(Long id, AsistenciaAlumnoRequestDTO requestDTO) {
        AsistenciaAlumno asistencia = asistenciaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Asistencia no encontrada con ID: " + id));

        // Validar que el estudiante existe
        estudianteRepository.findByIdOptional(requestDTO.getEstudianteId())
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado con ID: " + requestDTO.getEstudianteId()));

        // Validar que el horario existe
        horarioRepository.findByIdOptional(requestDTO.getHorarioId())
                .orElseThrow(() -> new NotFoundException("Horario no encontrado con ID: " + requestDTO.getHorarioId()));

        // Validar que si el estado es TARDANZA, debe tener minutos de tardanza
        if ("TARDANZA".equals(requestDTO.getEstado()) && 
            (requestDTO.getMinutosTardanza() == null || requestDTO.getMinutosTardanza() <= 0)) {
            throw new BusinessException("Debe especificar los minutos de tardanza cuando el estado es TARDANZA");
        }

        asistenciaMapper.updateEntityFromDTO(requestDTO, asistencia);
        asistenciaRepository.persist(asistencia);
        return asistenciaMapper.toResponseDTO(asistencia);
    }

    @Transactional
    public void delete(Long id) {
        AsistenciaAlumno asistencia = asistenciaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Asistencia no encontrada con ID: " + id));

        // Soft delete
        asistencia.setActive(false);
        asistenciaRepository.persist(asistencia);
    }
}

