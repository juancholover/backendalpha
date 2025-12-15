package upeu.edu.pe.core.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.core.application.dto.TipoLocalizacionRequestDTO;
import upeu.edu.pe.core.application.dto.TipoLocalizacionResponseDTO;
import upeu.edu.pe.core.application.mapper.TipoLocalizacionMapper;
import upeu.edu.pe.core.domain.commands.ActualizarTipoLocalizacionCommand;
import upeu.edu.pe.core.domain.commands.CrearTipoLocalizacionCommand;
import upeu.edu.pe.core.domain.entities.TipoLocalizacion;
import upeu.edu.pe.core.domain.services.TipoLocalizacionService;
import upeu.edu.pe.core.domain.usecases.ActualizarTipoLocalizacionUseCase;
import upeu.edu.pe.core.domain.usecases.CrearTipoLocalizacionUseCase;
import upeu.edu.pe.core.domain.usecases.EliminarTipoLocalizacionUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para tipos de localización.
 */
@Path("/api/v1/tipos-localizacion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tipos de Localización", description = "Catálogo de tipos de espacios físicos y virtuales")
public class TipoLocalizacionController {

    @Inject
    CrearTipoLocalizacionUseCase crearUseCase;

    @Inject
    ActualizarTipoLocalizacionUseCase actualizarUseCase;

    @Inject
    EliminarTipoLocalizacionUseCase eliminarUseCase;

    @Inject
    TipoLocalizacionService tipoLocalizacionService;

    @Inject
    TipoLocalizacionMapper tipoLocalizacionMapper;

    @GET
    @Operation(summary = "Listar todos los tipos de localización")
    public Response getAll() {
        List<TipoLocalizacionResponseDTO> tipos = tipoLocalizacionService.findAll();
        return Response.ok(ApiResponse.success("Tipos de localización obtenidos", tipos)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener tipo de localización por ID")
    public Response getById(@PathParam("id") Long id) {
        TipoLocalizacionResponseDTO tipo = tipoLocalizacionService.findById(id);
        return Response.ok(ApiResponse.success("Tipo de localización obtenido", tipo)).build();
    }

    @POST
    @Operation(summary = "Crear tipo de localización")
    public Response create(@Valid TipoLocalizacionRequestDTO dto) {
        CrearTipoLocalizacionCommand command = new CrearTipoLocalizacionCommand(
                dto.getCodigo(),
                dto.getNombre(),
                dto.getPadreId(),
                dto.getNivelJerarquia(),
                dto.getPermiteAsignacion());

        TipoLocalizacion tipoLocalizacion = crearUseCase.execute(command);
        TipoLocalizacionResponseDTO response = tipoLocalizacionMapper.toResponseDTO(tipoLocalizacion);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Tipo de localización creado", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar tipo de localización")
    public Response update(@PathParam("id") Long id, @Valid TipoLocalizacionRequestDTO dto) {
        ActualizarTipoLocalizacionCommand command = new ActualizarTipoLocalizacionCommand(
                id,
                dto.getNombre());

        TipoLocalizacion tipoLocalizacion = actualizarUseCase.execute(command);
        TipoLocalizacionResponseDTO response = tipoLocalizacionMapper.toResponseDTO(tipoLocalizacion);

        return Response.ok(ApiResponse.success("Tipo de localización actualizado", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar tipo de localización")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Tipo de localización eliminado", null)).build();
    }
}
