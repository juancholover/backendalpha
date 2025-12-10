package upeu.edu.pe.people.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import upeu.edu.pe.people.application.dto.ProfesorRequestDTO;
import upeu.edu.pe.people.application.dto.ProfesorResponseDTO;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.people.domain.entities.Profesor;

/**
 * Mapper para convertir entre Profesor entity y DTOs
 * Ahora Profesor se conecta directamente a Persona, no a Empleado
 */
@Mapper(componentModel = "cdi")
public interface ProfesorMapper {

    /**
     * Convierte RequestDTO a Entity
     * Ignora empleado (se setea manualmente en el service)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empleado", ignore = true)
    @Mapping(target = "unidadOrganizativa", ignore = true)
    @Mapping(target = "condicionDocente", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Profesor toEntity(ProfesorRequestDTO dto);

    /**
     * Convierte Entity a ResponseDTO
     */
    @Mapping(target = "empleadoId", source = "empleado.id")
    @Mapping(target = "codigoEmpleado", source = "empleado.codigoEmpleado")
    @Mapping(target = "cargoEmpleado", source = "empleado.cargo")
    @Mapping(target = "personaId", source = "empleado.persona.id")
    @Mapping(target = "nombreCompleto", source = "profesor", qualifiedByName = "buildNombreCompleto")
    @Mapping(target = "tipoDocumento", source = "empleado.persona.tipoDocumento")
    @Mapping(target = "numeroDocumento", source = "empleado.persona.numeroDocumento")
    @Mapping(target = "email", source = "empleado.persona.email")
    @Mapping(target = "telefono", source = "empleado.persona.telefono")
    @Mapping(target = "aniosServicio", constant = "0")
    @Mapping(target = "tituloProfesional", constant = "")
    @Mapping(target = "universidadProcedencia", constant = "")
    @Mapping(target = "fechaIngresoDocente", ignore = true)
    @Mapping(target = "numeroPublicaciones", constant = "0")
    @Mapping(target = "numeroProyectos", constant = "0")
    @Mapping(target = "areasInvestigacion", constant = "")
    @Mapping(target = "codigoCtiVitae", constant = "")
    @Mapping(target = "observaciones", constant = "")
    ProfesorResponseDTO toResponseDTO(Profesor profesor);

    /**
     * Actualiza entity existente con datos del DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empleado", ignore = true)
    @Mapping(target = "unidadOrganizativa", ignore = true)
    @Mapping(target = "condicionDocente", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDTO(ProfesorRequestDTO dto, @org.mapstruct.MappingTarget Profesor profesor);

    /**
     * Construye nombre completo desde empleado.persona
     */
    @Named("buildNombreCompleto")
    default String buildNombreCompleto(Profesor profesor) {
        if (profesor.getEmpleado() == null || profesor.getEmpleado().getPersona() == null) {
            return null;
        }
        
        Persona persona = profesor.getEmpleado().getPersona();
        String nombres = persona.getNombres();
        String apellidoPaterno = persona.getApellidoPaterno();
        String apellidoMaterno = persona.getApellidoMaterno();
        
        return String.format("%s %s %s", 
            apellidoPaterno != null ? apellidoPaterno : "",
            apellidoMaterno != null ? apellidoMaterno : "",
            nombres != null ? nombres : ""
        ).trim();
    }
}

