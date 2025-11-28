package upeu.edu.pe.academic.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.HorarioRequestDTO;
import upeu.edu.pe.academic.application.dto.HorarioResponseDTO;
import upeu.edu.pe.academic.application.mapper.HorarioMapper;
import upeu.edu.pe.academic.domain.entities.Horario;
import upeu.edu.pe.academic.domain.usecases.*;

import java.util.List;

@ApplicationScoped
public class HorarioApplicationService {

    @Inject
    CrearHorarioUseCase crearUseCase;

    @Inject
    ActualizarHorarioUseCase actualizarUseCase;

    @Inject
    BuscarHorarioUseCase buscarUseCase;

    @Inject
    EliminarHorarioUseCase eliminarUseCase;

    @Inject
    ValidarCruceHorarioUseCase validarCruceUseCase;

    @Inject
    HorarioMapper mapper;

    @Transactional
    public HorarioResponseDTO create(HorarioRequestDTO dto) {
        Horario horario = crearUseCase.execute(
                dto.getUniversidadId(),
                dto.getCursoOfertadoId(),
                dto.getDiaSemana(),
                dto.getHoraInicio(),
                dto.getHoraFin(),
                dto.getLocalizacionId(),
                dto.getTipoSesion(),
                dto.getObservaciones());
        return mapper.toResponseDTO(horario);
    }

    @Transactional
    public HorarioResponseDTO update(Long id, HorarioRequestDTO dto) {
        Horario horario = actualizarUseCase.execute(
                id,
                dto.getDiaSemana(),
                dto.getHoraInicio(),
                dto.getHoraFin(),
                dto.getLocalizacionId(),
                dto.getTipoSesion(),
                dto.getObservaciones());
        return mapper.toResponseDTO(horario);
    }

    @Transactional
    public void delete(Long id) {
        eliminarUseCase.execute(id);
    }

    public HorarioResponseDTO findById(Long id) {
        return mapper.toResponseDTO(buscarUseCase.findById(id));
    }

    public List<HorarioResponseDTO> findByUniversidad(Long universidadId) {
        return mapper.toResponseDTOList(buscarUseCase.findByUniversidad(universidadId));
    }

    public List<HorarioResponseDTO> findByCursoOfertado(Long cursoOfertadoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByCursoOfertado(cursoOfertadoId));
    }

    public List<HorarioResponseDTO> findByEstudiante(Long estudianteId) {
        return mapper.toResponseDTOList(buscarUseCase.findByEstudiante(estudianteId));
    }

    public List<HorarioResponseDTO> findByProfesor(Long profesorId) {
        return mapper.toResponseDTOList(buscarUseCase.findByProfesor(profesorId));
    }

    public List<HorarioResponseDTO> findByDiaSemana(Integer diaSemana, Long universidadId) {
        return mapper.toResponseDTOList(buscarUseCase.findByDiaSemana(diaSemana, universidadId));
    }

    public List<HorarioResponseDTO> findByLocalizacion(Long localizacionId) {
        return mapper.toResponseDTOList(buscarUseCase.findByLocalizacion(localizacionId));
    }

    public boolean tieneCreceHorario(Long estudianteId, Long cursoOfertadoId) {
        return validarCruceUseCase.execute(estudianteId, cursoOfertadoId);
    }
}
