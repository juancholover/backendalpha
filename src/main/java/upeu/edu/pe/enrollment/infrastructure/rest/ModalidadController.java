package upeu.edu.pe.enrollment.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.enrollment.application.dto.ModalidadRequestDTO;
import upeu.edu.pe.enrollment.application.dto.ModalidadResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.ModalidadMapper;
import upeu.edu.pe.enrollment.domain.commands.ActualizarModalidadCommand;
import upeu.edu.pe.enrollment.domain.commands.CrearModalidadCommand;
import upeu.edu.pe.enrollment.domain.entities.Modalidad;
import upeu.edu.pe.enrollment.domain.services.ModalidadService;
import upeu.edu.pe.enrollment.domain.usecases.ActualizarModalidadUseCase;
import upeu.edu.pe.enrollment.domain.usecases.CrearModalidadUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para gestión de Modalidades.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/modalidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Modalidades", description = "Gestión de modalidades de cursos")
public class ModalidadController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearModalidadUseCase crearUseCase;

    @Inject
    ActualizarModalidadUseCase actualizarUseCase;

    // Service para operaciones de lectura
    @Inject
    ModalidadService modalidadService;

    @Inject
    ModalidadMapper modalidadMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar modalidad por ID")
    public Response buscarPorId(@PathParam("id") Long id) {
        ModalidadResponseDTO modalidad = modalidadService.buscarPorId(id);
        return Response.ok(ApiResponse.success("Modalidad encontrada", modalidad)).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    @Operation(summary = "Buscar modalidad por código")
    public Response buscarPorCodigo(
            @PathParam("codigo") String codigo,
            @QueryParam("universidadId") Long universidadId) {
        ModalidadResponseDTO modalidad = modalidadService.buscarPorCodigo(codigo, universidadId);
        return Response.ok(ApiResponse.success("Modalidad encontrada", modalidad)).build();
    }

    @GET
    @Path("/universidad/{universidadId}")
    @Operation(summary = "Listar modalidades por universidad")
    public Response listarPorUniversidad(@PathParam("universidadId") Long universidadId) {
        List<ModalidadResponseDTO> modalidades = modalidadService.listarPorUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Modalidades obtenidas", modalidades)).build();
    }

    @GET
    @Path("/requieren-aula")
    @Operation(summary = "Listar modalidades que requieren aula")
    public Response listarRequierenAula(@QueryParam("universidadId") Long universidadId) {
        List<ModalidadResponseDTO> modalidades = modalidadService.listarRequierenAula(universidadId);
        return Response.ok(ApiResponse.success("Modalidades que requieren aula", modalidades)).build();
    }

    @GET
    @Path("/requieren-plataforma")
    @Operation(summary = "Listar modalidades que requieren plataforma")
    public Response listarRequierenPlataforma(@QueryParam("universidadId") Long universidadId) {
        List<ModalidadResponseDTO> modalidades = modalidadService.listarRequierenPlataforma(universidadId);
        return Response.ok(ApiResponse.success("Modalidades que requieren plataforma", modalidades)).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar modalidades por nombre")
    public Response buscarPorNombre(
            @QueryParam("q") String nombre,
            @QueryParam("universidadId") Long universidadId) {
        List<ModalidadResponseDTO> modalidades = modalidadService.buscarPorNombre(nombre, universidadId);
        return Response.ok(ApiResponse.success("Resultados de búsqueda", modalidades)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear modalidad")
    public Response crear(@Valid ModalidadRequestDTO dto) {
        CrearModalidadCommand command = new CrearModalidadCommand(
                dto.getCodigo(),
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getRequiereAula(),
                dto.getRequierePlataforma(),
                dto.getPorcentajePresencialidad(),
                dto.getColorHex());

        Modalidad modalidad = crearUseCase.execute(command);
        ModalidadResponseDTO response = modalidadMapper.toResponseDTO(modalidad);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Modalidad creada", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar modalidad")
    public Response actualizar(@PathParam("id") Long id, @Valid ModalidadRequestDTO dto) {
        ActualizarModalidadCommand command = new ActualizarModalidadCommand(
                id,
                dto.getCodigo(),
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getRequiereAula(),
                dto.getRequierePlataforma(),
                dto.getPorcentajePresencialidad(),
                dto.getColorHex());

        Modalidad modalidad = actualizarUseCase.execute(command);
        ModalidadResponseDTO response = modalidadMapper.toResponseDTO(modalidad);

        return Response.ok(ApiResponse.success("Modalidad actualizada", response)).build();
    }
}
