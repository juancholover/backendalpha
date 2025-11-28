package upeu.edu.pe.academic.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.CursoOfertadoRequestDTO;
import upeu.edu.pe.academic.application.dto.CursoOfertadoResponseDTO;
import upeu.edu.pe.academic.application.mapper.CursoOfertadoMapper;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;
import upeu.edu.pe.academic.domain.usecases.ActualizarCursoOfertadoUseCase;
import upeu.edu.pe.academic.domain.usecases.BuscarCursoOfertadoUseCase;
import upeu.edu.pe.academic.domain.usecases.CrearCursoOfertadoUseCase;
import upeu.edu.pe.academic.domain.usecases.EliminarCursoOfertadoUseCase;
import java.util.List;

@ApplicationScoped
public class CursoOfertadoApplicationService {

    @Inject
    CrearCursoOfertadoUseCase crearUseCase;

    @Inject
    ActualizarCursoOfertadoUseCase actualizarUseCase;

    @Inject
    BuscarCursoOfertadoUseCase buscarUseCase;

    @Inject
    EliminarCursoOfertadoUseCase eliminarUseCase;

    @Inject
    CursoOfertadoMapper mapper;

    @Transactional
    public CursoOfertadoResponseDTO create(CursoOfertadoRequestDTO dto) {
        CursoOfertado cursoOfertado = crearUseCase.execute(
                dto.getUniversidadId(),
                dto.getPlanCursoId(),
                dto.getPeriodoAcademicoId(),
                dto.getCodigoSeccion(),
                dto.getCapacidadMaxima(),
                dto.getVacantesDisponibles(),
                dto.getModalidad(),
                dto.getProfesorId(),
                dto.getLocalizacionId());
        return mapper.toResponseDTO(cursoOfertado);
    }

    @Transactional
    public CursoOfertadoResponseDTO update(Long id, CursoOfertadoRequestDTO dto) {
        CursoOfertado cursoOfertado = actualizarUseCase.execute(
                id,
                dto.getUniversidadId(),
                dto.getPlanCursoId(),
                dto.getPeriodoAcademicoId(),
                dto.getCodigoSeccion(),
                dto.getCapacidadMaxima(),
                dto.getVacantesDisponibles(),
                dto.getModalidad(),
                dto.getProfesorId(),
                dto.getLocalizacionId(),
                dto.getEstado(),
                dto.getObservaciones());
        return mapper.toResponseDTO(cursoOfertado);
    }

    @Transactional
    public void delete(Long id) {
        eliminarUseCase.execute(id);
    }

    public CursoOfertadoResponseDTO findById(Long id) {
        return mapper.toResponseDTO(buscarUseCase.findById(id));
    }

    public List<CursoOfertadoResponseDTO> findByUniversidad(Long universidadId) {
        return mapper.toResponseDTOList(buscarUseCase.findByUniversidad(universidadId));
    }

    public List<CursoOfertadoResponseDTO> findByPeriodoAcademico(Long periodoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByPeriodoAcademico(periodoId));
    }

    public List<CursoOfertadoResponseDTO> findByPlanAcademico(Long planId) {
        return mapper.toResponseDTOList(buscarUseCase.findByPlanAcademico(planId));
    }

    public List<CursoOfertadoResponseDTO> findByPlanCurso(Long planCursoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByPlanCurso(planCursoId));
    }

    public List<CursoOfertadoResponseDTO> findByCurso(Long cursoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByCurso(cursoId));
    }

    public List<CursoOfertadoResponseDTO> findByProfesor(Long profesorId) {
        return mapper.toResponseDTOList(buscarUseCase.findByProfesor(profesorId));
    }

    public List<CursoOfertadoResponseDTO> findAbiertasByPeriodoAndUniversidad(Long periodoId, Long universidadId) {
        return mapper.toResponseDTOList(buscarUseCase.findAbiertasByPeriodoAndUniversidad(periodoId, universidadId));
    }

    public List<CursoOfertadoResponseDTO> findByModalidadAndPeriodo(String modalidad, Long periodoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByModalidadAndPeriodo(modalidad, periodoId));
    }

    public List<CursoOfertadoResponseDTO> findConVacantesByPeriodo(Long periodoId) {
        return mapper.toResponseDTOList(buscarUseCase.findConVacantesByPeriodo(periodoId));
    }
}
