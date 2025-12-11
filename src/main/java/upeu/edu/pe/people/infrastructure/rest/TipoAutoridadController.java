package upeu.edu.pe.people.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.people.application.dto.CreateTipoAutoridadDTO;
import upeu.edu.pe.people.application.dto.TipoAutoridadDTO;
import upeu.edu.pe.people.application.mapper.TipoAutoridadMapper;
import upeu.edu.pe.people.domain.entities.TipoAutoridad;
import upeu.edu.pe.people.domain.services.TipoAutoridadService;
import upeu.edu.pe.people.domain.usecases.CrearTipoAutoridadUseCase;
import upeu.edu.pe.people.domain.usecases.EliminarTipoAutoridadUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para tipos de autoridad.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/tipos-autoridad")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tipos de Autoridad", description = "Gestión de tipos de autoridades")
public class TipoAutoridadController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearTipoAutoridadUseCase crearUseCase;

    @Inject
    EliminarTipoAutoridadUseCase eliminarUseCase;

    // Service para operaciones de lectura
    @Inject
    TipoAutoridadService tipoAutoridadService;

    @Inject
    TipoAutoridadMapper mapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/universidad/{universidadId}")
    @Operation(summary = "Listar tipos de autoridad por universidad")
    public Response findByUniversidadId(@PathParam("universidadId") Long universidadId) {
        List<TipoAutoridadDTO> tipos = tipoAutoridadService.findByUniversidadId(universidadId);
        return Response.ok(ApiResponse.success("Tipos de autoridad", tipos)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar tipo por ID")
    public Response findById(@PathParam("id") Long id) {
        TipoAutoridadDTO tipo = tipoAutoridadService.findById(id);
        return Response.ok(ApiResponse.success("Tipo de autoridad encontrado", tipo)).build();
    }

    @GET
    @Path("/universidad/{universidadId}/maxima-autoridad")
    @Operation(summary = "Obtener máxima autoridad")
    public Response findMaximaAutoridad(@PathParam("universidadId") Long universidadId) {
        TipoAutoridadDTO tipo = tipoAutoridadService.findMaximaAutoridad(universidadId);
        return Response.ok(ApiResponse.success("Máxima autoridad", tipo)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear tipo de autoridad")
    public Response create(@Valid CreateTipoAutoridadDTO dto) {
        TipoAutoridad tipo = crearUseCase.execute(
                dto.getCodigo(),
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getNivelJerarquia());
        TipoAutoridadDTO response = mapper.toDTO(tipo);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Tipo de autoridad creado", response))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar tipo de autoridad")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Tipo de autoridad eliminado", null)).build();
    }
}
