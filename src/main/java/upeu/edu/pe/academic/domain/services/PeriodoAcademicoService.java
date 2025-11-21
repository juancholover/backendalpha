package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.PeriodoAcademicoRequestDTO;
import upeu.edu.pe.academic.application.dto.PeriodoAcademicoResponseDTO;
import upeu.edu.pe.academic.application.mapper.PeriodoAcademicoMapper;
import upeu.edu.pe.academic.domain.entities.PeriodoAcademico;
import upeu.edu.pe.academic.domain.repositories.PeriodoAcademicoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class PeriodoAcademicoService {

    @Inject
    PeriodoAcademicoRepository periodoRepository;

    @Inject
    PeriodoAcademicoMapper periodoMapper;

    public List<PeriodoAcademicoResponseDTO> findByUniversidad(Long universidadId) {
        List<PeriodoAcademico> periodos = periodoRepository.findByUniversidad(universidadId);
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
        PeriodoAcademico periodo = periodoRepository.findActualByUniversidad(universidadId)
                .orElseThrow(() -> new NotFoundException("No hay período académico actual configurado"));
        return periodoMapper.toResponseDTO(periodo);
    }

    public PeriodoAcademicoResponseDTO findByCodigoAndUniversidad(String codigo, Long universidadId) {
        PeriodoAcademico periodo = periodoRepository.findByCodigoAndUniversidad(codigo, universidadId)
                .orElseThrow(() -> new NotFoundException("Período académico no encontrado: " + codigo));
        return periodoMapper.toResponseDTO(periodo);
    }

    public List<PeriodoAcademicoResponseDTO> findByAnioAndUniversidad(Integer anio, Long universidadId) {
        List<PeriodoAcademico> periodos = periodoRepository.findByAnioAndUniversidad(anio, universidadId);
        return periodoMapper.toResponseDTOList(periodos);
    }

    public List<PeriodoAcademicoResponseDTO> findByEstadoAndUniversidad(String estado, Long universidadId) {
        List<PeriodoAcademico> periodos = periodoRepository.findByEstadoAndUniversidad(estado, universidadId);
        return periodoMapper.toResponseDTOList(periodos);
    }

    public List<PeriodoAcademicoResponseDTO> findActivosAndUniversidad(Long universidadId) {
        List<PeriodoAcademico> periodos = periodoRepository.findActivosAndUniversidad(universidadId);
        return periodoMapper.toResponseDTOList(periodos);
    }

    @Transactional
    public PeriodoAcademicoResponseDTO create(PeriodoAcademicoRequestDTO requestDTO) {
        // Validar que no exista un período con el mismo código
        if (periodoRepository.existsByCodigoAndUniversidad(requestDTO.getCodigoPeriodo(), requestDTO.getUniversidadId())) {
            throw new BusinessException("Ya existe un período con el código: " + requestDTO.getCodigoPeriodo());
        }

        // Validar fechas
        if (requestDTO.getFechaFin().isBefore(requestDTO.getFechaInicio())) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        if (requestDTO.getFechaFinMatricula() != null && requestDTO.getFechaInicioMatricula() != null) {
            if (requestDTO.getFechaFinMatricula().isBefore(requestDTO.getFechaInicioMatricula())) {
                throw new BusinessException("La fecha fin de matrícula debe ser posterior a la fecha inicio");
            }
        }

        PeriodoAcademico periodo = periodoMapper.toEntity(requestDTO);
        
        upeu.edu.pe.academic.domain.entities.Universidad universidad = new upeu.edu.pe.academic.domain.entities.Universidad();
        universidad.setId(requestDTO.getUniversidadId());
        periodo.setUniversidad(universidad);

        // Si se marca como actual, desmarcar los demás
        if (requestDTO.getEsActual()) {
            periodoRepository.desmarcarTodosComoActual(requestDTO.getUniversidadId());
        }

        periodoRepository.persist(periodo);
        return periodoMapper.toResponseDTO(periodo);
    }

    @Transactional
    public PeriodoAcademicoResponseDTO update(Long id, PeriodoAcademicoRequestDTO requestDTO) {
        PeriodoAcademico periodo = periodoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Período académico no encontrado con ID: " + id));

        // Validar código duplicado (excepto el actual)
        periodoRepository.findByCodigoAndUniversidad(requestDTO.getCodigoPeriodo(), requestDTO.getUniversidadId())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BusinessException("Ya existe un período con el código: " + requestDTO.getCodigoPeriodo());
                    }
                });

        // Validar fechas
        if (requestDTO.getFechaFin().isBefore(requestDTO.getFechaInicio())) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        periodoMapper.updateEntityFromDTO(requestDTO, periodo);

        // Si se marca como actual, desmarcar los demás
        if (requestDTO.getEsActual() && !periodo.getEsActual()) {
            periodoRepository.desmarcarTodosComoActual(requestDTO.getUniversidadId());
        }

        periodoRepository.persist(periodo);
        return periodoMapper.toResponseDTO(periodo);
    }

    @Transactional
    public void delete(Long id) {
        PeriodoAcademico periodo = periodoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Período académico no encontrado con ID: " + id));

        // Validar que no tenga cursos ofertados asociados
        if (periodo.getCursosOfertados() != null && !periodo.getCursosOfertados().isEmpty()) {
            throw new BusinessException("No se puede eliminar el período porque tiene " + 
                periodo.getCursosOfertados().size() + " cursos ofertados asociados");
        }

        // Soft delete
        periodo.setActive(false);
        periodoRepository.persist(periodo);
    }

    @Transactional
    public PeriodoAcademicoResponseDTO marcarComoActual(Long id) {
        PeriodoAcademico periodo = periodoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Período académico no encontrado con ID: " + id));

        periodoRepository.desmarcarTodosComoActual(periodo.getUniversidad().getId());
        periodo.setEsActual(true);
        periodoRepository.persist(periodo);

        return periodoMapper.toResponseDTO(periodo);
    }
}
