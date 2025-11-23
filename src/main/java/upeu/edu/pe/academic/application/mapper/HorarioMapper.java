package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.HorarioRequestDTO;
import upeu.edu.pe.academic.application.dto.HorarioResponseDTO;
import upeu.edu.pe.academic.domain.entities.Horario;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HorarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "cursoOfertado", ignore = true)
    @Mapping(target = "localizacion", ignore = true)
    Horario toEntity(HorarioRequestDTO dto);

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "universidadNombre", source = "universidad.nombre")
    @Mapping(target = "cursoOfertadoId", source = "cursoOfertado.id")
    @Mapping(target = "cursoOfertadoCodigoSeccion", source = "cursoOfertado.codigoSeccion")
    @Mapping(target = "cursoNombre", source = "cursoOfertado.planCurso.curso.nombre")
    @Mapping(target = "cursoCodigo", source = "cursoOfertado.planCurso.curso.codigoCurso")
    @Mapping(target = "profesorId", source = "cursoOfertado.profesor.id")
    @Mapping(target = "profesorNombre", expression = "java(getNombreProfesor(entity))")
    @Mapping(target = "nombreDia", expression = "java(entity.getNombreDia())")
    @Mapping(target = "duracionMinutos", expression = "java(entity.getDuracionMinutos())")
    @Mapping(target = "localizacionId", source = "localizacion.id")
    @Mapping(target = "localizacionNombre", source = "localizacion.nombre")
    @Mapping(target = "localizacionCodigo", source = "localizacion.codigo")
    HorarioResponseDTO toResponseDTO(Horario entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "cursoOfertado", ignore = true)
    @Mapping(target = "localizacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(HorarioRequestDTO dto, @MappingTarget Horario entity);

    default String getNombreProfesor(Horario horario) {
        if (horario.getCursoOfertado() != null && 
            horario.getCursoOfertado().getProfesor() != null && 
            horario.getCursoOfertado().getProfesor().getPersona() != null) {
            
            var persona = horario.getCursoOfertado().getProfesor().getPersona();
            return persona.getApellidoPaterno() + " " + 
                   persona.getApellidoMaterno() + ", " + 
                   persona.getNombres();
        }
        return null;
    }
}
