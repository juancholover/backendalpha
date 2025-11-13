package upeu.edu.pe.academic.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import upeu.edu.pe.academic.application.dto.ProfesorRequestDTO;
import upeu.edu.pe.academic.application.dto.ProfesorResponseDTO;
import upeu.edu.pe.academic.domain.entities.Empleado;
import upeu.edu.pe.academic.domain.entities.Profesor;

import java.time.LocalDate;
import java.time.Period;

/**
 * Mapper para convertir entre Profesor entity y DTOs (Versión simplificada)
 * Mapea solo los campos existentes en la entidad Profesor
 */
@Mapper(componentModel = "cdi")
public interface ProfesorMapper {

    /**
     * Convierte RequestDTO a Entity
     * Ignora empleado (se setea manualmente en el service)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empleado", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "condicionDocente", constant = "ORDINARIO")
    @Mapping(target = "codigoRenacyt", source = "codigoRenacyt")
    Profesor toEntity(ProfesorRequestDTO dto);

    /**
     * Convierte Entity a ResponseDTO
     * Incluye información del empleado y años de servicio
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
    @Mapping(target = "aniosServicio", source = "profesor", qualifiedByName = "calculateAniosServicio")
    @Mapping(target = "tituloProfesional", constant = "")
    @Mapping(target = "universidadProcedencia", constant = "")
    @Mapping(target = "fechaIngresoDocente", source = "empleado.fechaIngreso")
    @Mapping(target = "numeroPublicaciones", constant = "0")
    @Mapping(target = "numeroProyectos", constant = "0")
    @Mapping(target = "areasInvestigacion", constant = "")
    @Mapping(target = "codigoCtiVitae", constant = "")
    @Mapping(target = "observaciones", constant = "")
    ProfesorResponseDTO toResponseDTO(Profesor profesor);

    /**
     * Actualiza entity existente con datos del DTO
     * Ignora empleado (no se puede cambiar)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empleado", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "condicionDocente", ignore = true)
    void updateEntityFromDTO(ProfesorRequestDTO dto, @org.mapstruct.MappingTarget Profesor profesor);

    /**
     * Construye nombre completo desde empleado->persona
     */
    @Named("buildNombreCompleto")
    default String buildNombreCompleto(Profesor profesor) {
        if (profesor.getEmpleado() == null || profesor.getEmpleado().getPersona() == null) {
            return null;
        }
        
        Empleado empleado = profesor.getEmpleado();
        String nombres = empleado.getPersona().getNombres();
        String apellidoPaterno = empleado.getPersona().getApellidoPaterno();
        String apellidoMaterno = empleado.getPersona().getApellidoMaterno();
        
        return String.format("%s %s %s", 
            apellidoPaterno != null ? apellidoPaterno : "",
            apellidoMaterno != null ? apellidoMaterno : "",
            nombres != null ? nombres : ""
        ).trim();
    }

    /**
     * Calcula años de servicio desde fecha de ingreso del empleado
     */
    @Named("calculateAniosServicio")
    default Integer calculateAniosServicio(Profesor profesor) {
        if (profesor.getEmpleado() == null || profesor.getEmpleado().getFechaIngreso() == null) {
            return 0;
        }
        
        LocalDate fechaIngreso = profesor.getEmpleado().getFechaIngreso();
        LocalDate now = LocalDate.now();
        
        return Period.between(fechaIngreso, now).getYears();
    }
}
