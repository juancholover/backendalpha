package upeu.edu.pe.curriculum.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import upeu.edu.pe.curriculum.application.dto.SilaboPublicacionResponseDTO;
import upeu.edu.pe.curriculum.domain.entities.SilaboPublicacion;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SilaboPublicacionMapper {

    @Mapping(target = "plantillaId", source = "plantilla.id")
    @Mapping(target = "plantillaEstado", source = "plantilla.estado")
    @Mapping(target = "localizacionId", source = "localizacion.id")
    @Mapping(target = "localizacionNombre", source = "localizacion.nombre")
    @Mapping(target = "localizacionCodigo", source = "localizacion.codigo")
    @Mapping(target = "silaboCampusId", source = "silaboCampus.id")
    SilaboPublicacionResponseDTO toResponseDTO(SilaboPublicacion entity);
}
