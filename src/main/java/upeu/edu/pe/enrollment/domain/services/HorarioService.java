package upeu.edu.pe.enrollment.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.enrollment.application.dto.HorarioResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.HorarioMapper;
import upeu.edu.pe.enrollment.domain.entities.Horario;
import upeu.edu.pe.enrollment.domain.repositories.HorarioRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas de horarios.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class HorarioService {

    @Inject
    HorarioRepository horarioRepository;

    @Inject
    HorarioMapper horarioMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<HorarioResponseDTO> findByUniversidad(Long universidadId) {
        return horarioRepository.findAllActive()
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    public HorarioResponseDTO findById(Long id) {
        Horario horario = horarioRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Horario no encontrado con ID: " + id));
        return horarioMapper.toResponseDTO(horario);
    }

    public List<HorarioResponseDTO> findByCursoOfertado(Long cursoOfertadoId) {
        return horarioRepository.findByCursoOfertado(cursoOfertadoId)
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    public List<HorarioResponseDTO> findByEstudiante(Long estudianteId) {
        return horarioRepository.findByEstudiante(estudianteId)
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    public List<HorarioResponseDTO> findByProfesor(Long profesorId) {
        return horarioRepository.findByProfesor(profesorId)
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    public List<HorarioResponseDTO> findByDiaSemana(Integer diaSemana, Long universidadId) {
        return horarioRepository.findByDiaSemana(diaSemana)
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    public List<HorarioResponseDTO> findByLocalizacion(Long localizacionId) {
        return horarioRepository.findByLocalizacion(localizacionId)
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    public boolean tieneCreceHorario(Long estudianteId, Long cursoOfertadoId) {
        List<Horario> horariosNuevoCurso = horarioRepository.findByCursoOfertado(cursoOfertadoId);

        for (Horario nuevoHorario : horariosNuevoCurso) {
            List<Horario> cruces = horarioRepository.findCrucesEstudiante(
                    estudianteId,
                    nuevoHorario.getDiaSemana(),
                    nuevoHorario.getHoraInicio(),
                    nuevoHorario.getHoraFin());

            if (!cruces.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public Horario getEntityById(Long id) {
        return horarioRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Horario no encontrado con ID: " + id));
    }
}
