package upeu.edu.pe.enrollment.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.enrollment.application.dto.MatriculaRequestDTO;
import upeu.edu.pe.enrollment.application.dto.MatriculaResponseDTO;
import upeu.edu.pe.enrollment.domain.entities.Matricula;

import java.math.BigDecimal;
import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MatriculaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "cursoOfertado", ignore = true)
    @Mapping(target = "evaluacionNotas", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    Matricula toEntity(MatriculaRequestDTO dto);

    @Mapping(target = "estudianteId", source = "estudiante.id")
    @Mapping(target = "estudianteNombre", source = "estudiante.persona.nombres")
    @Mapping(target = "estudianteApellido", source = "estudiante.persona.apellidoPaterno")
    @Mapping(target = "estudianteCodigo", source = "estudiante.codigoEstudiante")
    @Mapping(target = "seccionId", source = "cursoOfertado.id")
    @Mapping(target = "seccionCodigo", source = "cursoOfertado.codigoSeccion")
    @Mapping(target = "cursoId", source = "cursoOfertado.planCurso.curso.id")
    @Mapping(target = "cursoNombre", source = "cursoOfertado.planCurso.curso.nombre")
    @Mapping(target = "cursoCodigo", source = "cursoOfertado.planCurso.curso.codigoCurso")
    @Mapping(target = "periodoAcademicoId", source = "cursoOfertado.periodoAcademico.id")
    @Mapping(target = "periodoAcademicoNombre", source = "cursoOfertado.periodoAcademico.nombre")
    @Mapping(target = "periodoAcademicoCodigo", source = "cursoOfertado.periodoAcademico.nombre")
    @Mapping(target = "profesorId", source = "cursoOfertado.profesor.id")
    @Mapping(target = "profesorNombre", source = "cursoOfertado.profesor.empleado.persona.nombres")
    @Mapping(target = "profesorApellido", source = "cursoOfertado.profesor.empleado.persona.apellidoPaterno")
    @Mapping(target = "notaFinal", expression = "java(calculateNotaFinal(entity))")
    @Mapping(target = "estadoAprobacion", expression = "java(calculateEstadoAprobacion(entity))")
    @Mapping(target = "inasistencias", expression = "java(calculateInasistencias(entity))")
    MatriculaResponseDTO toResponseDTO(Matricula entity);

    List<MatriculaResponseDTO> toResponseDTOList(List<Matricula> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estudiante", ignore = true)
    @Mapping(target = "cursoOfertado", ignore = true)
    @Mapping(target = "evaluacionNotas", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(MatriculaRequestDTO dto, @MappingTarget Matricula entity);

    /**
     * Calcula la nota final promediando las notas de evaluación
     */
    default BigDecimal calculateNotaFinal(Matricula matricula) {
        if (matricula == null || matricula.getEvaluacionNotas() == null || matricula.getEvaluacionNotas().isEmpty()) {
            return null;
        }
        
        double promedio = matricula.getEvaluacionNotas().stream()
            .filter(nota -> nota.getNotaFinal() != null)
            .mapToDouble(nota -> nota.getNotaFinal().doubleValue())
            .average()
            .orElse(0.0);
            
        return BigDecimal.valueOf(promedio);
    }

    /**
     * Calcula el estado de aprobación basado en la nota final
     */
    default String calculateEstadoAprobacion(Matricula matricula) {
        BigDecimal notaFinal = calculateNotaFinal(matricula);
        if (notaFinal == null) {
            return "PENDIENTE";
        }
        return notaFinal.compareTo(BigDecimal.valueOf(10.5)) >= 0 ? "APROBADO" : "DESAPROBADO";
    }

    /**
     * Calcula el total de inasistencias del estudiante en el curso matriculado
     * Cuenta las asistencias registradas como "AUSENTE" o "FALTA"
     */
    default Integer calculateInasistencias(Matricula matricula) {
        if (matricula == null || matricula.getCursoOfertado() == null || 
            matricula.getCursoOfertado().getHorarios() == null || 
            matricula.getEstudiante() == null) {
            return 0;
        }
        
        // Contar asistencias marcadas como ausentes en todos los horarios del curso
        return (int) matricula.getCursoOfertado().getHorarios().stream()
            .flatMap(horario -> horario.getAsistencias() != null ? 
                horario.getAsistencias().stream() : java.util.stream.Stream.empty())
            .filter(asistencia -> asistencia.getEstudiante() != null && 
                asistencia.getEstudiante().getId().equals(matricula.getEstudiante().getId()))
            .filter(asistencia -> "AUSENTE".equalsIgnoreCase(asistencia.getEstado()) || 
                "FALTA".equalsIgnoreCase(asistencia.getEstado()))
            .count();
    }
}

