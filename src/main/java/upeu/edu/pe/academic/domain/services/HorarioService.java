package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import upeu.edu.pe.academic.application.dto.HorarioRequestDTO;
import upeu.edu.pe.academic.application.dto.HorarioResponseDTO;
import upeu.edu.pe.academic.application.mapper.HorarioMapper;
import upeu.edu.pe.academic.domain.entities.*;
import upeu.edu.pe.academic.domain.repositories.*;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class HorarioService {

    @Inject
    HorarioRepository horarioRepository;

    @Inject
    UniversidadRepository universidadRepository;

    @Inject
    CursoOfertadoRepository cursoOfertadoRepository;

    @Inject
    LocalizacionRepository localizacionRepository;

    @Inject
    HorarioMapper horarioMapper;

    /**
     * Busca todos los horarios de una universidad
     */
    public List<HorarioResponseDTO> findByUniversidad(Long universidadId) {
        return horarioRepository.findByUniversidad(universidadId)
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca horarios por ID
     */
    public HorarioResponseDTO findById(Long id) {
        Horario horario = horarioRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con ID: " + id));
        return horarioMapper.toResponseDTO(horario);
    }

    /**
     * Busca horarios de un curso ofertado
     */
    public List<HorarioResponseDTO> findByCursoOfertado(Long cursoOfertadoId) {
        return horarioRepository.findByCursoOfertado(cursoOfertadoId)
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca horarios de un estudiante (todos los cursos matriculados)
     */
    public List<HorarioResponseDTO> findByEstudiante(Long estudianteId) {
        return horarioRepository.findByEstudiante(estudianteId)
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca horarios de un profesor
     */
    public List<HorarioResponseDTO> findByProfesor(Long profesorId) {
        return horarioRepository.findByProfesor(profesorId)
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca horarios por día de la semana
     */
    public List<HorarioResponseDTO> findByDiaSemana(Integer diaSemana, Long universidadId) {
        return horarioRepository.findByDiaSemanaAndUniversidad(diaSemana, universidadId)
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca horarios en una localización
     */
    public List<HorarioResponseDTO> findByLocalizacion(Long localizacionId) {
        return horarioRepository.findByLocalizacion(localizacionId)
                .stream()
                .map(horarioMapper::toResponseDTO)
                .toList();
    }

    /**
     * Crea un nuevo horario con validaciones completas
     */
    @Transactional
    public HorarioResponseDTO create(@Valid HorarioRequestDTO dto) {
        // Validar universidad
        Universidad universidad = universidadRepository.findByIdOptional(dto.getUniversidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Universidad no encontrada"));

        // Validar curso ofertado
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(dto.getCursoOfertadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso ofertado no encontrado"));

        // Validar que el curso pertenece a la universidad
        if (!cursoOfertado.getUniversidad().getId().equals(dto.getUniversidadId())) {
            throw new BusinessException("El curso ofertado no pertenece a la universidad especificada");
        }

        // Validar horas
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
            throw new BusinessException("La hora de inicio debe ser anterior a la hora de fin");
        }

        // Validar cruce de horarios para el profesor
        List<Horario> crucesProfesor = horarioRepository.findCrucesProfesor(
                cursoOfertado.getProfesor().getId(),
                dto.getDiaSemana(),
                dto.getHoraInicio(),
                dto.getHoraFin()
        );

        if (!crucesProfesor.isEmpty()) {
            throw new BusinessException(
                    "El profesor ya tiene clase el " + getNombreDia(dto.getDiaSemana()) +
                    " de " + dto.getHoraInicio() + " a " + dto.getHoraFin()
            );
        }

        // Validar cruce de horarios en la localización (si se especifica)
        if (dto.getLocalizacionId() != null) {
            Localizacion localizacion = localizacionRepository.findByIdOptional(dto.getLocalizacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Localización no encontrada"));

            List<Horario> crucesAula = horarioRepository.findCrucesLocalizacion(
                    dto.getLocalizacionId(),
                    dto.getDiaSemana(),
                    dto.getHoraInicio(),
                    dto.getHoraFin(),
                    null
            );

            if (!crucesAula.isEmpty()) {
                throw new BusinessException(
                        "El aula " + localizacion.getNombre() + " ya está ocupada el " +
                        getNombreDia(dto.getDiaSemana()) + " de " + dto.getHoraInicio() + " a " + dto.getHoraFin()
                );
            }
        }

        // Validar que no exista el mismo horario
        if (horarioRepository.existeHorario(dto.getCursoOfertadoId(), dto.getDiaSemana(), dto.getHoraInicio())) {
            throw new BusinessException("Ya existe un horario para este curso en el mismo día y hora");
        }

        // Crear entidad
        Horario horario = horarioMapper.toEntity(dto);
        horario.setUniversidad(universidad);
        horario.setCursoOfertado(cursoOfertado);

        if (dto.getLocalizacionId() != null) {
            Localizacion localizacion = localizacionRepository.findByIdOptional(dto.getLocalizacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Localización no encontrada"));
            horario.setLocalizacion(localizacion);
        }

        horarioRepository.persist(horario);

        return horarioMapper.toResponseDTO(horario);
    }

    /**
     * Actualiza un horario existente
     */
    @Transactional
    public HorarioResponseDTO update(Long id, @Valid HorarioRequestDTO dto) {
        Horario horario = horarioRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

        // Validar horas
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())) {
            throw new BusinessException("La hora de inicio debe ser anterior a la hora de fin");
        }

        // Validar cruce con profesor (excluyendo el horario actual)
        CursoOfertado cursoOfertado = horario.getCursoOfertado();
        List<Horario> crucesProfesor = horarioRepository.findCrucesProfesor(
                cursoOfertado.getProfesor().getId(),
                dto.getDiaSemana(),
                dto.getHoraInicio(),
                dto.getHoraFin()
        );

        // Excluir el horario actual de los cruces
        crucesProfesor = crucesProfesor.stream()
                .filter(h -> !h.getId().equals(id))
                .toList();

        if (!crucesProfesor.isEmpty()) {
            throw new BusinessException(
                    "El profesor ya tiene clase el " + getNombreDia(dto.getDiaSemana()) +
                    " de " + dto.getHoraInicio() + " a " + dto.getHoraFin()
            );
        }

        // Validar cruce con localización (si se especifica)
        if (dto.getLocalizacionId() != null) {
            Localizacion localizacion = localizacionRepository.findByIdOptional(dto.getLocalizacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Localización no encontrada"));

            List<Horario> crucesAula = horarioRepository.findCrucesLocalizacion(
                    dto.getLocalizacionId(),
                    dto.getDiaSemana(),
                    dto.getHoraInicio(),
                    dto.getHoraFin(),
                    id // Excluir el horario actual
            );

            if (!crucesAula.isEmpty()) {
                throw new BusinessException(
                        "El aula " + localizacion.getNombre() + " ya está ocupada el " +
                        getNombreDia(dto.getDiaSemana()) + " de " + dto.getHoraInicio() + " a " + dto.getHoraFin()
                );
            }

            horario.setLocalizacion(localizacion);
        } else {
            horario.setLocalizacion(null);
        }

        // Actualizar campos
        horarioMapper.updateEntityFromDto(dto, horario);
        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHoraInicio(dto.getHoraInicio());
        horario.setHoraFin(dto.getHoraFin());
        horario.setTipoSesion(dto.getTipoSesion());
        horario.setObservaciones(dto.getObservaciones());

        horarioRepository.persist(horario);

        return horarioMapper.toResponseDTO(horario);
    }

    /**
     * Elimina (lógicamente) un horario
     */
    @Transactional
    public void delete(Long id) {
        Horario horario = horarioRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

        horario.setActive(false);
        horarioRepository.persist(horario);
    }

    /**
     * Valida si un estudiante tiene cruce de horarios al matricularse
     */
    public boolean tieneCreceHorario(Long estudianteId, Long cursoOfertadoId) {
        List<Horario> horariosNuevoCurso = horarioRepository.findByCursoOfertado(cursoOfertadoId);
        
        for (Horario nuevoHorario : horariosNuevoCurso) {
            List<Horario> cruces = horarioRepository.findCrucesEstudiante(
                    estudianteId,
                    nuevoHorario.getDiaSemana(),
                    nuevoHorario.getHoraInicio(),
                    nuevoHorario.getHoraFin()
            );
            
            if (!cruces.isEmpty()) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Obtiene el nombre del día de la semana
     */
    private String getNombreDia(Integer diaSemana) {
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
