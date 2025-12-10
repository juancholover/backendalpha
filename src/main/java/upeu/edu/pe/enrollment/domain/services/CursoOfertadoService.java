package upeu.edu.pe.enrollment.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.enrollment.application.dto.CursoOfertadoResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.CursoOfertadoMapper;
import upeu.edu.pe.enrollment.domain.entities.CursoOfertado;
import upeu.edu.pe.enrollment.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas de cursos ofertados.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class CursoOfertadoService {

    @Inject
    CursoOfertadoRepository cursoOfertadoRepository;

    @Inject
    CursoOfertadoMapper cursoOfertadoMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<CursoOfertadoResponseDTO> findAllActive() {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findAllActive();
        return cursoOfertadoMapper.toResponseDTOList(cursosOfertados);
    }

    public List<CursoOfertadoResponseDTO> findByPeriodoAcademico(Long periodoId) {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findByPeriodoAcademico(periodoId);
        return cursoOfertadoMapper.toResponseDTOList(cursosOfertados);
    }

    public List<CursoOfertadoResponseDTO> findByPlanAcademico(Long planId) {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findByPlanAcademico(planId);
        return cursoOfertadoMapper.toResponseDTOList(cursosOfertados);
    }

    public List<CursoOfertadoResponseDTO> findByPlanCurso(Long planCursoId) {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findByPlanCurso(planCursoId);
        return cursoOfertadoMapper.toResponseDTOList(cursosOfertados);
    }

    public List<CursoOfertadoResponseDTO> findByCurso(Long cursoId) {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findByCurso(cursoId);
        return cursoOfertadoMapper.toResponseDTOList(cursosOfertados);
    }

    public List<CursoOfertadoResponseDTO> findByProfesor(Long profesorId) {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findByProfesor(profesorId);
        return cursoOfertadoMapper.toResponseDTOList(cursosOfertados);
    }

    public List<CursoOfertadoResponseDTO> findAbiertasByPeriodo(Long periodoId) {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findAbiertasByPeriodo(periodoId);
        return cursoOfertadoMapper.toResponseDTOList(cursosOfertados);
    }

    public List<CursoOfertadoResponseDTO> findByModalidadAndPeriodo(String modalidad, Long periodoId) {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findByModalidadAndPeriodo(modalidad, periodoId);
        return cursoOfertadoMapper.toResponseDTOList(cursosOfertados);
    }

    public List<CursoOfertadoResponseDTO> findConVacantesByPeriodo(Long periodoId) {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findConVacantesByPeriodo(periodoId);
        return cursoOfertadoMapper.toResponseDTOList(cursosOfertados);
    }

    public CursoOfertadoResponseDTO findById(Long id) {
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Curso ofertado no encontrado con ID: " + id));
        return cursoOfertadoMapper.toResponseDTO(cursoOfertado);
    }

    public CursoOfertadoResponseDTO findByCodigoAndPeriodo(String codigoSeccion, Long periodoId) {
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByCodigoAndPeriodo(codigoSeccion, periodoId)
                .orElseThrow(() -> new NotFoundException("Curso ofertado no encontrado con código: " + codigoSeccion));
        return cursoOfertadoMapper.toResponseDTO(cursoOfertado);
    }

    /**
     * Obtener entidad por ID (para uso interno)
     */
    public CursoOfertado getEntityById(Long id) {
        return cursoOfertadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Curso ofertado no encontrado con ID: " + id));
    }
}
