package upeu.edu.pe.curriculum.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import upeu.edu.pe.curriculum.application.dto.SilaboPlantillaResponseDTO;
import upeu.edu.pe.curriculum.domain.entities.SilaboPlantilla;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SilaboPlantillaMapper {

    @Mapping(target = "silaboId", source = "silabo.id")
    @Mapping(target = "silaboCursoNombre", source = "silabo.curso.nombre")
    @Mapping(target = "silaboCursoCodigo", source = "silabo.curso.codigoCurso")
    @Mapping(target = "silaboAnioAcademico", source = "silabo.anioAcademico")
    @Mapping(target = "cantidadPublicaciones", expression = "java(entity.getPublicaciones() != null ? entity.getPublicaciones().size() : 0)")
    SilaboPlantillaResponseDTO toResponseDTO(SilaboPlantilla entity);
}
