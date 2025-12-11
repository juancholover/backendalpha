package upeu.edu.pe.core.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.core.application.dto.TipoUnidadRequestDTO;
import upeu.edu.pe.core.application.dto.TipoUnidadResponseDTO;
import upeu.edu.pe.core.application.mapper.TipoUnidadMapper;
import upeu.edu.pe.core.domain.commands.ActualizarTipoUnidadCommand;
import upeu.edu.pe.core.domain.commands.CrearTipoUnidadCommand;
import upeu.edu.pe.core.domain.entities.TipoUnidad;
import upeu.edu.pe.core.domain.services.TipoUnidadService;
import upeu.edu.pe.core.domain.usecases.ActualizarTipoUnidadUseCase;
import upeu.edu.pe.core.domain.usecases.CrearTipoUnidadUseCase;
import upeu.edu.pe.core.domain.usecases.EliminarTipoUnidadUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para tipos de unidad organizativa.
 */
@Path("/api/v1/tipos-unidad")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tipos de Unidad", description = "Catálogo de tipos de unidades organizativas")
public class TipoUnidadController {

    @Inject
    CrearTipoUnidadUseCase crearUseCase;

    @Inject
    ActualizarTipoUnidadUseCase actualizarUseCase;

    @Inject
    EliminarTipoUnidadUseCase eliminarUseCase;

    @Inject
    TipoUnidadService tipoUnidadService;

    @Inject
    TipoUnidadMapper tipoUnidadMapper;

    @GET
    @Operation(summary = "Listar todos los tipos de unidad")
    public Response getAll() {
        List<TipoUnidadResponseDTO> tipos = tipoUnidadService.findAll();
        return Response.ok(ApiResponse.success("Tipos de unidad obtenidos", tipos)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener tipo de unidad por ID")
    public Response getById(@PathParam("id") Long id) {
        TipoUnidadResponseDTO tipo = tipoUnidadService.findById(id);
        return Response.ok(ApiResponse.success("Tipo de unidad obtenido", tipo)).build();
    }

    @POST
    @Operation(summary = "Crear tipo de unidad")
    public Response create(@Valid TipoUnidadRequestDTO dto) {
        CrearTipoUnidadCommand command = new CrearTipoUnidadCommand(
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getNivel());

        TipoUnidad tipoUnidad = crearUseCase.execute(command);
        TipoUnidadResponseDTO response = tipoUnidadMapper.toResponseDTO(tipoUnidad);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Tipo de unidad creado", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar tipo de unidad")
    public Response update(@PathParam("id") Long id, @Valid TipoUnidadRequestDTO dto) {
        ActualizarTipoUnidadCommand command = new ActualizarTipoUnidadCommand(
                id,
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getNivel());

        TipoUnidad tipoUnidad = actualizarUseCase.execute(command);
        TipoUnidadResponseDTO response = tipoUnidadMapper.toResponseDTO(tipoUnidad);

        return Response.ok(ApiResponse.success("Tipo de unidad actualizado", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar tipo de unidad")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Tipo de unidad eliminado", null)).build();
    }
}
