package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.AsistenciaAlumnoRequestDTO;
import upeu.edu.pe.academic.application.dto.AsistenciaAlumnoResponseDTO;
import upeu.edu.pe.academic.domain.entities.AsistenciaAlumno;

import java.util.List;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AsistenciaAlumnoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad.id", source = "universidadId")
    @Mapping(target = "estudiante.id", source = "estudianteId")
    @Mapping(target = "horario.id", source = "horarioId")
    @Mapping(target = "active", constant = "true")
    AsistenciaAlumno toEntity(AsistenciaAlumnoRequestDTO dto);

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "estudianteId", source = "estudiante.id")
    @Mapping(target = "estudianteNombre", expression = "java(getNombreEstudiante(entity))")
    @Mapping(target = "estudianteCodigo", source = "estudiante.codigoEstudiante")
    @Mapping(target = "horarioId", source = "horario.id")
    @Mapping(target = "cursoNombre", expression = "java(getCursoNombre(entity))")
    @Mapping(target = "cursoCodigo", expression = "java(getCursoCodigo(entity))")
    @Mapping(target = "diaSemana", source = "horario.diaSemana")
    @Mapping(target = "nombreDia", expression = "java(entity.getHorario().getNombreDia())")
    @Mapping(target = "horaInicio", source = "horario.horaInicio")
    @Mapping(target = "horaFin", source = "horario.horaFin")
    @Mapping(target = "localizacionNombre", expression = "java(getLocalizacionNombre(entity))")
    AsistenciaAlumnoResponseDTO toResponseDTO(AsistenciaAlumno entity);

    List<AsistenciaAlumnoResponseDTO> toResponseDTOList(List<AsistenciaAlumno> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad.id", source = "universidadId")
    @Mapping(target = "estudiante.id", source = "estudianteId")
    @Mapping(target = "horario.id", source = "horarioId")
    void updateEntityFromDTO(AsistenciaAlumnoRequestDTO dto, @MappingTarget AsistenciaAlumno entity);

    /**
     * Obtener nombre completo del estudiante
     */
    default String getNombreEstudiante(AsistenciaAlumno asistencia) {
        if (asistencia.getEstudiante() != null && asistencia.getEstudiante().getPersona() != null) {
            var persona = asistencia.getEstudiante().getPersona();
            return persona.getNombres() + " " + persona.getApellidoPaterno() + " " + persona.getApellidoMaterno();
        }
        return null;
    }

    /**
     * Obtener nombre del curso desde horario
     */
    default String getCursoNombre(AsistenciaAlumno asistencia) {
        if (asistencia.getHorario() != null && 
            asistencia.getHorario().getCursoOfertado() != null && 
            asistencia.getHorario().getCursoOfertado().getPlanCurso() != null &&
            asistencia.getHorario().getCursoOfertado().getPlanCurso().getCurso() != null) {
            return asistencia.getHorario().getCursoOfertado().getPlanCurso().getCurso().getNombre();
        }
        return null;
    }

    /**
     * Obtener código del curso desde horario
     */
    default String getCursoCodigo(AsistenciaAlumno asistencia) {
        if (asistencia.getHorario() != null && 
            asistencia.getHorario().getCursoOfertado() != null && 
            asistencia.getHorario().getCursoOfertado().getPlanCurso() != null &&
            asistencia.getHorario().getCursoOfertado().getPlanCurso().getCurso() != null) {
            return asistencia.getHorario().getCursoOfertado().getPlanCurso().getCurso().getCodigoCurso();
        }
        return null;
    }

    /**
     * Obtener nombre de la localización desde horario
     */
    default String getLocalizacionNombre(AsistenciaAlumno asistencia) {
        if (asistencia.getHorario() != null && asistencia.getHorario().getLocalizacion() != null) {
            return asistencia.getHorario().getLocalizacion().getNombre();
        }
        return null;
    }
}
