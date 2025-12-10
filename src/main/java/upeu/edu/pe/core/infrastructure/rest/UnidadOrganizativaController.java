package upeu.edu.pe.core.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.core.application.dto.UnidadOrganizativaRequestDTO;
import upeu.edu.pe.core.application.dto.UnidadOrganizativaResponseDTO;
import upeu.edu.pe.core.application.mapper.UnidadOrganizativaMapper;
import upeu.edu.pe.core.domain.commands.CrearUnidadOrganizativaCommand;
import upeu.edu.pe.core.domain.commands.ActualizarUnidadOrganizativaCommand;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.core.domain.services.UnidadOrganizativaService;
import upeu.edu.pe.core.domain.usecases.CrearUnidadOrganizativaUseCase;
import upeu.edu.pe.core.domain.usecases.ActualizarUnidadOrganizativaUseCase;
import upeu.edu.pe.core.domain.usecases.EliminarUnidadOrganizativaUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para gestión de unidades organizativas.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/unidades-organizativas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Unidades Organizativas", description = "Gestión de facultades, escuelas y departamentos")
public class UnidadOrganizativaController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearUnidadOrganizativaUseCase crearUnidadUseCase;

    @Inject
    ActualizarUnidadOrganizativaUseCase actualizarUnidadUseCase;

    @Inject
    EliminarUnidadOrganizativaUseCase eliminarUnidadUseCase;

    // Service para operaciones de lectura
    @Inject
    UnidadOrganizativaService unidadService;

    @Inject
    UnidadOrganizativaMapper unidadMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar todas las unidades organizativas")
    public Response getAll() {
        List<UnidadOrganizativaResponseDTO> unidades = unidadService.findAll();
        return Response.ok(ApiResponse.success("Unidades organizativas obtenidas exitosamente", unidades)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener unidad organizativa por ID")
    public Response getById(@PathParam("id") Long id) {
        UnidadOrganizativaResponseDTO unidad = unidadService.findById(id);
        return Response.ok(ApiResponse.success("Unidad organizativa obtenida exitosamente", unidad)).build();
    }

    @GET
    @Path("/universidad/{universidadId}")
    @Operation(summary = "Listar unidades por universidad")
    public Response getByUniversidad(@PathParam("universidadId") Long universidadId) {
        List<UnidadOrganizativaResponseDTO> unidades = unidadService.findByUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Unidades organizativas por universidad obtenidas", unidades)).build();
    }

    @GET
    @Path("/tipo-unidad/{tipoUnidadId}")
    @Operation(summary = "Listar unidades por tipo")
    public Response getByTipoUnidad(@PathParam("tipoUnidadId") Long tipoUnidadId) {
        List<UnidadOrganizativaResponseDTO> unidades = unidadService.findByTipoUnidad(tipoUnidadId);
        return Response.ok(ApiResponse.success("Unidades organizativas por tipo obtenidas", unidades)).build();
    }

    @GET
    @Path("/raiz/universidad/{universidadId}")
    @Operation(summary = "Listar unidades raíz")
    public Response getRootUnidades(@PathParam("universidadId") Long universidadId) {
        List<UnidadOrganizativaResponseDTO> unidades = unidadService.findRootUnidades(universidadId);
        return Response.ok(ApiResponse.success("Unidades raíz obtenidas", unidades)).build();
    }

    @GET
    @Path("/hijas/{unidadPadreId}")
    @Operation(summary = "Listar unidades hijas")
    public Response getByUnidadPadre(@PathParam("unidadPadreId") Long unidadPadreId) {
        List<UnidadOrganizativaResponseDTO> unidades = unidadService.findByUnidadPadre(unidadPadreId);
        return Response.ok(ApiResponse.success("Unidades hijas obtenidas", unidades)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear unidad organizativa")
    public Response create(@Valid UnidadOrganizativaRequestDTO requestDTO) {

        // Convertir DTO a Command
        CrearUnidadOrganizativaCommand command = new CrearUnidadOrganizativaCommand(
                requestDTO.getTipoUnidadId(),
                requestDTO.getCodigo(),
                requestDTO.getNombre(),
                requestDTO.getSigla(), // DTO usa 'sigla'
                requestDTO.getDescripcion(),
                requestDTO.getUnidadPadreId(),
                requestDTO.getLocalizacionId());

        // Ejecutar Use Case
        UnidadOrganizativa unidad = crearUnidadUseCase.execute(command);

        // Convertir a DTO de respuesta
        UnidadOrganizativaResponseDTO response = unidadMapper.toResponseDTO(unidad);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Unidad organizativa creada exitosamente", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar unidad organizativa")
    public Response update(@PathParam("id") Long id, @Valid UnidadOrganizativaRequestDTO requestDTO) {

        // Convertir DTO a Command
        ActualizarUnidadOrganizativaCommand command = new ActualizarUnidadOrganizativaCommand(
                id,
                requestDTO.getTipoUnidadId(),
                requestDTO.getCodigo(),
                requestDTO.getNombre(),
                requestDTO.getSigla(), // DTO usa 'sigla'
                requestDTO.getDescripcion(),
                requestDTO.getUnidadPadreId(),
                requestDTO.getLocalizacionId());

        // Ejecutar Use Case
        UnidadOrganizativa unidad = actualizarUnidadUseCase.execute(command);

        // Convertir a DTO de respuesta
        UnidadOrganizativaResponseDTO response = unidadMapper.toResponseDTO(unidad);

        return Response.ok(ApiResponse.success("Unidad organizativa actualizada exitosamente", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar unidad organizativa")
    public Response delete(@PathParam("id") Long id) {

        // Ejecutar Use Case
        eliminarUnidadUseCase.execute(id);

        return Response.ok(ApiResponse.success("Unidad organizativa eliminada exitosamente", null)).build();
    }
}
