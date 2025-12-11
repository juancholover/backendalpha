package upeu.edu.pe.enrollment.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.enrollment.application.dto.PeriodoAcademicoResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.PeriodoAcademicoMapper;
import upeu.edu.pe.enrollment.domain.entities.PeriodoAcademico;
import upeu.edu.pe.enrollment.domain.repositories.PeriodoAcademicoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas de periodos académicos.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class PeriodoAcademicoService {

    @Inject
    PeriodoAcademicoRepository periodoRepository;

    @Inject
    PeriodoAcademicoMapper periodoMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<PeriodoAcademicoResponseDTO> findByUniversidad(Long universidadId) {
        List<PeriodoAcademico> periodos = periodoRepository.findAllActive();
        return periodoMapper.toResponseDTOList(periodos);
    }

    public PeriodoAcademicoResponseDTO findById(Long id) {
        PeriodoAcademico periodo = periodoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Período académico no encontrado con ID: " + id));

        PeriodoAcademicoResponseDTO response = periodoMapper.toResponseDTO(periodo);
        response.setCantidadSecciones(periodo.getCursosOfertados() != null ? periodo.getCursosOfertados().size() : 0);

        return response;
    }

    public PeriodoAcademicoResponseDTO findActualByUniversidad(Long universidadId) {
        PeriodoAcademico periodo = periodoRepository.findActual()
                .orElseThrow(() -> new NotFoundException("No hay período académico actual configurado"));
        return periodoMapper.toResponseDTO(periodo);
    }

    public PeriodoAcademicoResponseDTO findByCodigoAndUniversidad(String codigo, Long universidadId) {
        PeriodoAcademico periodo = periodoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new NotFoundException("Período académico no encontrado: " + codigo));
        return periodoMapper.toResponseDTO(periodo);
    }

    public List<PeriodoAcademicoResponseDTO> findByAnioAndUniversidad(Integer anio, Long universidadId) {
        List<PeriodoAcademico> periodos = periodoRepository.findByAnio(anio);
        return periodoMapper.toResponseDTOList(periodos);
    }

    public List<PeriodoAcademicoResponseDTO> findByEstadoAndUniversidad(String estado, Long universidadId) {
        List<PeriodoAcademico> periodos = periodoRepository.findByEstado(estado);
        return periodoMapper.toResponseDTOList(periodos);
    }

    public List<PeriodoAcademicoResponseDTO> findActivosAndUniversidad(Long universidadId) {
        List<PeriodoAcademico> periodos = periodoRepository.findActivos();
        return periodoMapper.toResponseDTOList(periodos);
    }

    public PeriodoAcademico getEntityById(Long id) {
        return periodoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Período académico no encontrado con ID: " + id));
    }
}
