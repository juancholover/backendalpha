package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.academic.application.dto.CursoOfertadoRequestDTO;
import upeu.edu.pe.academic.application.dto.CursoOfertadoResponseDTO;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CursoOfertadoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "planCurso", ignore = true)
    @Mapping(target = "periodoAcademico", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "localizacion", ignore = true)
    @Mapping(target = "vacantesDisponibles", ignore = true)
    @Mapping(target = "matriculas", ignore = true)
    @Mapping(target = "evaluacionCriterios", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    CursoOfertado toEntity(CursoOfertadoRequestDTO dto);

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "universidadNombre", source = "universidad.nombre")
    @Mapping(target = "planCursoId", source = "planCurso.id")
    @Mapping(target = "planAcademicoId", source = "planCurso.planAcademico.id")
    @Mapping(target = "planAcademicoCodigo", source = "planCurso.planAcademico.codigo")
    @Mapping(target = "cursoId", source = "planCurso.curso.id")
    @Mapping(target = "cursoNombre", source = "planCurso.curso.nombre")
    @Mapping(target = "cursoCodigo", source = "planCurso.curso.codigoCurso")
    @Mapping(target = "creditos", source = "planCurso.creditos")
    @Mapping(target = "ciclo", source = "planCurso.ciclo")
    @Mapping(target = "tipoCurso", source = "planCurso.tipoCurso")
    @Mapping(target = "periodoAcademicoId", source = "periodoAcademico.id")
    @Mapping(target = "periodoNombre", source = "periodoAcademico.nombre")
    @Mapping(target = "periodoCodigo", source = "periodoAcademico.codigoPeriodo")
    @Mapping(target = "profesorId", source = "profesor.id")
    @Mapping(target = "profesorNombre", expression = "java(getProfesorNombre(entity))")
    @Mapping(target = "localizacionId", source = "localizacion.id")
    @Mapping(target = "localizacionNombre", ignore = true)
    @Mapping(target = "cantidadMatriculados", ignore = true)
    @Mapping(target = "cantidadCriterios", ignore = true)
    CursoOfertadoResponseDTO toResponseDTO(CursoOfertado entity);

    List<CursoOfertadoResponseDTO> toResponseDTOList(List<CursoOfertado> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "planCurso", ignore = true)
    @Mapping(target = "periodoAcademico", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "localizacion", ignore = true)
    @Mapping(target = "vacantesDisponibles", ignore = true)
    @Mapping(target = "matriculas", ignore = true)
    @Mapping(target = "evaluacionCriterios", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(CursoOfertadoRequestDTO dto, @MappingTarget CursoOfertado entity);

    default String getProfesorNombre(CursoOfertado cursoOfertado) {
        if (cursoOfertado == null || cursoOfertado.getProfesor() == null || 
            cursoOfertado.getProfesor().getPersona() == null) {
            return null;
        }
        var p = cursoOfertado.getProfesor().getPersona();
        return String.format("%s %s %s", 
            p.getApellidoPaterno() != null ? p.getApellidoPaterno() : "",
            p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "",
            p.getNombres() != null ? p.getNombres() : ""
        ).trim();
    }
}
