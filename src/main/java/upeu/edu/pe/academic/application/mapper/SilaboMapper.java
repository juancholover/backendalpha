package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.*;
import upeu.edu.pe.academic.application.dto.SilaboRequestDTO;
import upeu.edu.pe.academic.application.dto.SilaboResponseDTO;
import upeu.edu.pe.academic.domain.entities.Silabo;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {SilaboUnidadMapper.class})
public interface SilaboMapper {

    /**
     * Convierte SilaboRequestDTO a Silabo (para crear)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "unidades", ignore = true)
    @Mapping(target = "historial", ignore = true)
    Silabo toEntity(SilaboRequestDTO dto);

    /**
     * Actualiza una entidad Silabo existente desde DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "curso", ignore = true)
    @Mapping(target = "universidad", ignore = true)
    @Mapping(target = "unidades", ignore = true)
    @Mapping(target = "historial", ignore = true)
    void updateEntityFromDto(SilaboRequestDTO dto, @MappingTarget Silabo entity);

    /**
     * Convierte Silabo a SilaboResponseDTO
     */
    @Mapping(target = "cursoId", source = "curso.id")
    @Mapping(target = "cursoNombre", source = "curso.nombre")
    @Mapping(target = "cursoCodigo", source = "curso.codigoCurso")
    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "totalSemanas", expression = "java(entity.getTotalSemanas())")
    @Mapping(target = "porcentajeEvaluacionTotal", expression = "java(entity.getPorcentajeEvaluacionTotal())")
    @Mapping(target = "cantidadUnidades", expression = "java(entity.getUnidades() != null ? entity.getUnidades().size() : 0)")
    @Mapping(target = "unidades", source = "unidades")
    SilaboResponseDTO toResponseDTO(Silabo entity);

    /**
     * Convierte Silabo a SilaboResponseDTO sin unidades (para listados)
     */
    @Mapping(target = "cursoId", source = "curso.id")
    @Mapping(target = "cursoNombre", source = "curso.nombre")
    @Mapping(target = "cursoCodigo", source = "curso.codigoCurso")
    @Mapping(target = "universidadId", source = "universidad.id")
    @Mapping(target = "totalSemanas", expression = "java(entity.getTotalSemanas())")
    @Mapping(target = "porcentajeEvaluacionTotal", expression = "java(entity.getPorcentajeEvaluacionTotal())")
    @Mapping(target = "cantidadUnidades", expression = "java(entity.getUnidades() != null ? entity.getUnidades().size() : 0)")
    @Mapping(target = "unidades", ignore = true)
    SilaboResponseDTO toResponseDTOWithoutUnidades(Silabo entity);
}
