package upeu.edu.pe.people.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.people.application.dto.AutoridadDTO;
import upeu.edu.pe.people.application.dto.CreateAutoridadDTO;
import upeu.edu.pe.people.application.dto.UpdateAutoridadDTO;
import upeu.edu.pe.people.application.mapper.AutoridadMapper;
import upeu.edu.pe.people.domain.commands.ActualizarAutoridadCommand;
import upeu.edu.pe.people.domain.commands.CrearAutoridadCommand;
import upeu.edu.pe.people.domain.entities.Autoridad;
import upeu.edu.pe.people.domain.services.AutoridadService;
import upeu.edu.pe.people.domain.usecases.ActualizarAutoridadUseCase;
import upeu.edu.pe.people.domain.usecases.CrearAutoridadUseCase;
import upeu.edu.pe.people.domain.usecases.EliminarAutoridadUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para autoridades.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/autoridades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autoridades", description = "Gestión de autoridades académicas")
public class AutoridadController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearAutoridadUseCase crearUseCase;

    @Inject
    ActualizarAutoridadUseCase actualizarUseCase;

    @Inject
    EliminarAutoridadUseCase eliminarUseCase;

    // Service para operaciones de lectura
    @Inject
    AutoridadService autoridadService;

    // Mapper
    @Inject
    AutoridadMapper autoridadMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/universidad/{universidadId}/activas")
    @Operation(summary = "Listar autoridades activas")
    public Response findActivasByUniversidadId(@PathParam("universidadId") Long universidadId) {
        List<AutoridadDTO> autoridades = autoridadService.findActivasByUniversidadId(universidadId);
        return Response.ok(ApiResponse.success("Autoridades activas", autoridades)).build();
    }

    @GET
    @Path("/universidad/{universidadId}/vigentes")
    @Operation(summary = "Listar autoridades vigentes")
    public Response findVigentesByUniversidadId(@PathParam("universidadId") Long universidadId) {
        List<AutoridadDTO> autoridades = autoridadService.findVigentesByUniversidadId(universidadId);
        return Response.ok(ApiResponse.success("Autoridades vigentes", autoridades)).build();
    }

    @GET
    @Path("/universidad/{universidadId}")
    @Operation(summary = "Listar autoridades por universidad")
    public Response findByUniversidadId(@PathParam("universidadId") Long universidadId) {
        List<AutoridadDTO> autoridades = autoridadService.findByUniversidadId(universidadId);
        return Response.ok(ApiResponse.success("Autoridades obtenidas", autoridades)).build();
    }

    @GET
    @Path("/persona/{personaId}")
    @Operation(summary = "Listar autoridades por persona")
    public Response findByPersonaId(@PathParam("personaId") Long personaId) {
        List<AutoridadDTO> autoridades = autoridadService.findByPersonaId(personaId);
        return Response.ok(ApiResponse.success("Historial de autoridades", autoridades)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar autoridad por ID")
    public Response findById(@PathParam("id") Long id) {
        AutoridadDTO autoridad = autoridadService.findById(id);
        return Response.ok(ApiResponse.success("Autoridad encontrada", autoridad)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear autoridad")
    public Response create(@Valid CreateAutoridadDTO dto) {
        CrearAutoridadCommand command = new CrearAutoridadCommand(
                dto.getPersonaId(),
                dto.getTipoAutoridadId(),
                null, // unidadOrganizativaId - puede agregarse al DTO si es necesario
                null, // programaAcademicoId - puede agregarse al DTO si es necesario
                dto.getFechaInicio(),
                dto.getFechaFin(),
                dto.getResolucionDesignacion(),
                dto.getObservaciones());

        Autoridad autoridad = crearUseCase.execute(command);
        AutoridadDTO response = autoridadMapper.toDTO(autoridad);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Autoridad creada exitosamente", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar autoridad")
    public Response update(@PathParam("id") Long id, @Valid UpdateAutoridadDTO dto) {
        ActualizarAutoridadCommand command = new ActualizarAutoridadCommand(
                id,
                dto.getPersonaId(),
                dto.getTipoAutoridadId(),
                null, // unidadOrganizativaId
                null, // programaAcademicoId
                dto.getFechaInicio(),
                dto.getFechaFin(),
                dto.getEsVigente(),
                dto.getResolucionDesignacion(),
                dto.getObservaciones());

        Autoridad autoridad = actualizarUseCase.execute(command);
        AutoridadDTO response = autoridadMapper.toDTO(autoridad);

        return Response.ok(ApiResponse.success("Autoridad actualizada exitosamente", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar autoridad")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Autoridad eliminada", null)).build();
    }
}
