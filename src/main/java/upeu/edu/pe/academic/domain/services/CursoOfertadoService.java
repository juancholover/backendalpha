package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.CursoOfertadoRequestDTO;
import upeu.edu.pe.academic.application.dto.CursoOfertadoResponseDTO;
import upeu.edu.pe.academic.application.mapper.CursoOfertadoMapper;
import upeu.edu.pe.academic.domain.entities.*;
import upeu.edu.pe.academic.domain.repositories.*;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class CursoOfertadoService {

    @Inject
    CursoOfertadoRepository cursoOfertadoRepository;

    @Inject
    UniversidadRepository universidadRepository;

    @Inject
    PlanAcademicoRepository planAcademicoRepository;

    @Inject
    PeriodoAcademicoRepository periodoAcademicoRepository;

    @Inject
    ProfesorRepository profesorRepository;

    @Inject
    LocalizacionRepository localizacionRepository;

    @Inject
    CursoOfertadoMapper cursoOfertadoMapper;

    public List<CursoOfertadoResponseDTO> findByUniversidad(Long universidadId) {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findByUniversidad(universidadId);
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

    public List<CursoOfertadoResponseDTO> findByProfesor(Long profesorId) {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findByProfesor(profesorId);
        return cursoOfertadoMapper.toResponseDTOList(cursosOfertados);
    }

    public List<CursoOfertadoResponseDTO> findAbiertasByPeriodoAndUniversidad(Long periodoId, Long universidadId) {
        List<CursoOfertado> cursosOfertados = cursoOfertadoRepository.findAbiertasByPeriodoAndUniversidad(periodoId, universidadId);
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

    public CursoOfertadoResponseDTO findByCodigoAndPeriodoAndUniversidad(String codigoSeccion, Long periodoId, Long universidadId) {
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByCodigoAndPeriodoAndUniversidad(codigoSeccion, periodoId, universidadId)
                .orElseThrow(() -> new NotFoundException("Curso ofertado no encontrado con código: " + codigoSeccion));
        return cursoOfertadoMapper.toResponseDTO(cursoOfertado);
    }

    @Transactional
    public CursoOfertadoResponseDTO create(CursoOfertadoRequestDTO requestDTO) {
        // Validar que no exista el curso ofertado
        if (cursoOfertadoRepository.existsByCodigoAndPeriodoAndPlan(
                requestDTO.getCodigoSeccion(), 
                requestDTO.getPeriodoAcademicoId(), 
                requestDTO.getPlanAcademicoId())) {
            throw new BusinessException("Ya existe un curso ofertado con el código: " + requestDTO.getCodigoSeccion() 
                    + " para este plan y período");
        }

        // Validar universidad
        Universidad universidad = universidadRepository.findByIdOptional(requestDTO.getUniversidadId())
                .orElseThrow(() -> new NotFoundException("Universidad no encontrada con ID: " + requestDTO.getUniversidadId()));

        // Validar plan académico
        PlanAcademico planAcademico = planAcademicoRepository.findByIdOptional(requestDTO.getPlanAcademicoId())
                .orElseThrow(() -> new NotFoundException("Plan académico no encontrado con ID: " + requestDTO.getPlanAcademicoId()));

        // Validar período académico
        PeriodoAcademico periodoAcademico = periodoAcademicoRepository.findByIdOptional(requestDTO.getPeriodoAcademicoId())
                .orElseThrow(() -> new NotFoundException("Período académico no encontrado con ID: " + requestDTO.getPeriodoAcademicoId()));

        // Validar vacantes disponibles
        Integer vacantesDisponibles = requestDTO.getVacantesDisponibles();
        if (vacantesDisponibles == null) {
            vacantesDisponibles = requestDTO.getCapacidadMaxima();
        } else if (vacantesDisponibles > requestDTO.getCapacidadMaxima()) {
            throw new BusinessException("Las vacantes disponibles no pueden exceder la capacidad máxima");
        }

        // Crear curso ofertado
        CursoOfertado cursoOfertado = cursoOfertadoMapper.toEntity(requestDTO);
        cursoOfertado.setUniversidad(universidad);
        cursoOfertado.setPlanAcademico(planAcademico);
        cursoOfertado.setPeriodoAcademico(periodoAcademico);
        cursoOfertado.setVacantesDisponibles(vacantesDisponibles);

        // Validar y asignar profesor si existe
        if (requestDTO.getProfesorId() != null) {
            Profesor profesor = profesorRepository.findByIdOptional(requestDTO.getProfesorId())
                    .orElseThrow(() -> new NotFoundException("Profesor no encontrado con ID: " + requestDTO.getProfesorId()));
            cursoOfertado.setProfesor(profesor);
        }

        // Validar y asignar localización si existe
        if (requestDTO.getLocalizacionId() != null) {
            Localizacion localizacion = localizacionRepository.findByIdOptional(requestDTO.getLocalizacionId())
                    .orElseThrow(() -> new NotFoundException("Localización no encontrada con ID: " + requestDTO.getLocalizacionId()));
            cursoOfertado.setLocalizacion(localizacion);
        }

        cursoOfertadoRepository.persist(cursoOfertado);
        return cursoOfertadoMapper.toResponseDTO(cursoOfertado);
    }

    @Transactional
    public CursoOfertadoResponseDTO update(Long id, CursoOfertadoRequestDTO requestDTO) {
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Curso ofertado no encontrado con ID: " + id));

        // Validar cambios de relaciones
        if (!cursoOfertado.getUniversidad().getId().equals(requestDTO.getUniversidadId())) {
            Universidad universidad = universidadRepository.findByIdOptional(requestDTO.getUniversidadId())
                    .orElseThrow(() -> new NotFoundException("Universidad no encontrada con ID: " + requestDTO.getUniversidadId()));
            cursoOfertado.setUniversidad(universidad);
        }

        if (!cursoOfertado.getPlanAcademico().getId().equals(requestDTO.getPlanAcademicoId())) {
            PlanAcademico planAcademico = planAcademicoRepository.findByIdOptional(requestDTO.getPlanAcademicoId())
                    .orElseThrow(() -> new NotFoundException("Plan académico no encontrado con ID: " + requestDTO.getPlanAcademicoId()));
            cursoOfertado.setPlanAcademico(planAcademico);
        }

        if (!cursoOfertado.getPeriodoAcademico().getId().equals(requestDTO.getPeriodoAcademicoId())) {
            PeriodoAcademico periodoAcademico = periodoAcademicoRepository.findByIdOptional(requestDTO.getPeriodoAcademicoId())
                    .orElseThrow(() -> new NotFoundException("Período académico no encontrado con ID: " + requestDTO.getPeriodoAcademicoId()));
            cursoOfertado.setPeriodoAcademico(periodoAcademico);
        }

        // Validar profesor
        if (requestDTO.getProfesorId() != null) {
            if (cursoOfertado.getProfesor() == null || !cursoOfertado.getProfesor().getId().equals(requestDTO.getProfesorId())) {
                Profesor profesor = profesorRepository.findByIdOptional(requestDTO.getProfesorId())
                        .orElseThrow(() -> new NotFoundException("Profesor no encontrado con ID: " + requestDTO.getProfesorId()));
                cursoOfertado.setProfesor(profesor);
            }
        } else {
            cursoOfertado.setProfesor(null);
        }

        // Validar localización
        if (requestDTO.getLocalizacionId() != null) {
            if (cursoOfertado.getLocalizacion() == null || !cursoOfertado.getLocalizacion().getId().equals(requestDTO.getLocalizacionId())) {
                Localizacion localizacion = localizacionRepository.findByIdOptional(requestDTO.getLocalizacionId())
                        .orElseThrow(() -> new NotFoundException("Localización no encontrada con ID: " + requestDTO.getLocalizacionId()));
                cursoOfertado.setLocalizacion(localizacion);
            }
        } else {
            cursoOfertado.setLocalizacion(null);
        }

        // Validar vacantes
        if (requestDTO.getVacantesDisponibles() != null && 
            requestDTO.getVacantesDisponibles() > requestDTO.getCapacidadMaxima()) {
            throw new BusinessException("Las vacantes disponibles no pueden exceder la capacidad máxima");
        }

        cursoOfertadoMapper.updateEntityFromDTO(requestDTO, cursoOfertado);
        cursoOfertadoRepository.persist(cursoOfertado);
        return cursoOfertadoMapper.toResponseDTO(cursoOfertado);
    }

    @Transactional
    public void delete(Long id) {
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Curso ofertado no encontrado con ID: " + id));

        // Verificar si tiene matrículas
        long matriculados = cursoOfertadoRepository.countMatriculados(id);
        if (matriculados > 0) {
            throw new BusinessException("No se puede eliminar el curso ofertado porque tiene " + matriculados + " estudiantes matriculados");
        }

        // Soft delete
        cursoOfertado.setActive(false);
        cursoOfertadoRepository.persist(cursoOfertado);
    }
}
