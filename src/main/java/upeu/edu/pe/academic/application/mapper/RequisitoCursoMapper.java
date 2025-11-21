package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.academic.application.dto.RequisitoCursoRequestDTO;
import upeu.edu.pe.academic.application.dto.RequisitoCursoResponseDTO;
import upeu.edu.pe.academic.domain.entities.RequisitoCurso;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RequisitoCursoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "cursoRequisito", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    RequisitoCurso toEntity(RequisitoCursoRequestDTO dto);

    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "universidadNombre", source = "universidad.nombre")
    @Mapping(target = "cursoId", source = "curso.id")
    @Mapping(target = "cursoNombre", source = "curso.nombre")
    @Mapping(target = "cursoCodigo", source = "curso.codigoCurso")
    @Mapping(target = "cursoRequisitoId", source = "cursoRequisito.id")
    @Mapping(target = "cursoRequisitoNombre", source = "cursoRequisito.nombre")
    @Mapping(target = "cursoRequisitoCodigo", source = "cursoRequisito.codigoCurso")
    RequisitoCursoResponseDTO toResponseDTO(RequisitoCurso entity);

    List<RequisitoCursoResponseDTO> toResponseDTOList(List<RequisitoCurso> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "cursoRequisito", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(RequisitoCursoRequestDTO dto, @MappingTarget RequisitoCurso entity);
}
