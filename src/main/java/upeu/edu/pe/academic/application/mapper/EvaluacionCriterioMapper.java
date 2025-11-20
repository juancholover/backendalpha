package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.academic.application.dto.EvaluacionCriterioRequestDTO;
import upeu.edu.pe.academic.application.dto.EvaluacionCriterioResponseDTO;
import upeu.edu.pe.academic.domain.entities.EvaluacionCriterio;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EvaluacionCriterioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cursoOfertado", ignore = true)
    @Mapping(target = "evaluacionNotas", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    EvaluacionCriterio toEntity(EvaluacionCriterioRequestDTO dto);

    @Mapping(target = "seccionId", source = "cursoOfertado.id")
    @Mapping(target = "seccionCodigo", source = "cursoOfertado.codigoSeccion")
    @Mapping(target = "cursoNombre", ignore = true)
    @Mapping(target = "cantidadNotas", ignore = true)
    @Mapping(target = "promedioNotas", ignore = true)
    EvaluacionCriterioResponseDTO toResponseDTO(EvaluacionCriterio entity);

    List<EvaluacionCriterioResponseDTO> toResponseDTOList(List<EvaluacionCriterio> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cursoOfertado", ignore = true)
    @Mapping(target = "evaluacionNotas", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(EvaluacionCriterioRequestDTO dto, @MappingTarget EvaluacionCriterio entity);
}
