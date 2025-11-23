package upeu.edu.pe.academic.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.academic.application.dto.CursoRequestDTO;
import upeu.edu.pe.academic.application.dto.CursoResponseDTO;
import upeu.edu.pe.academic.domain.services.CursoService;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

@Path("/api/v1/cursos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Cursos", description = "API para gestión de cursos académicos")
public class CursoController {

    @Inject
    CursoService cursoService;

    @GET
    @Operation(summary = "Listar todos los cursos", description = "Obtiene una lista de todos los cursos activos en el sistema")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente",
                     content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public Response findAll() {
        List<CursoResponseDTO> cursos = cursoService.findAll();
        return Response.ok(ApiResponse.success("Cursos listados exitosamente", cursos)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener curso por ID", description = "Obtiene los detalles de un curso específico por su ID")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Curso encontrado"),
        @APIResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public Response findById(
            @Parameter(description = "ID del curso", required = true)
            @PathParam("id") Long id) {
        CursoResponseDTO curso = cursoService.findById(id);
        return Response.ok(ApiResponse.success("Curso encontrado", curso)).build();
    }

    @GET
    @Path("/codigo/{codigoCurso}")
    @Operation(summary = "Buscar curso por código", description = "Busca un curso por su código único")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Curso encontrado"),
        @APIResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public Response findByCodigoCurso(
            @Parameter(description = "Código del curso", required = true, example = "SIS-101")
            @PathParam("codigoCurso") String codigoCurso) {
        CursoResponseDTO curso = cursoService.findByCodigoCurso(codigoCurso);
        return Response.ok(ApiResponse.success("Curso encontrado", curso)).build();
    }

    @GET
    @Path("/universidad/{universidadId}")
    @Operation(summary = "Listar cursos por universidad", description = "Obtiene todos los cursos de una universidad. Para cursos por plan académico usar PlanCurso API")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de cursos obtenida")
    })
    public Response findByUniversidad(
            @Parameter(description = "ID de la universidad", required = true)
            @PathParam("universidadId") Long universidadId) {
        List<CursoResponseDTO> cursos = cursoService.findByUniversidad(universidadId);
        return Response.ok(ApiResponse.success("Cursos de la universidad listados", cursos)).build();
    }

    @POST
    @Operation(summary = "Crear nuevo curso", description = "Registra un nuevo curso en el catálogo de la universidad (sin plan académico específico)")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Curso creado exitosamente"),
        @APIResponse(responseCode = "400", description = "Datos de entrada inválidos o regla de negocio violada"),
        @APIResponse(responseCode = "404", description = "Universidad no encontrada"),
        @APIResponse(responseCode = "409", description = "El curso ya existe")
    })
    public Response create(
            @Parameter(description = "Datos del curso a crear", required = true)
            @Valid CursoRequestDTO dto) {
        CursoResponseDTO curso = cursoService.create(dto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Curso creado exitosamente", curso))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar curso", description = "Actualiza los datos de un curso existente")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Curso actualizado exitosamente"),
        @APIResponse(responseCode = "400", description = "Datos de entrada inválidos o regla de negocio violada"),
        @APIResponse(responseCode = "404", description = "Curso no encontrado"),
        @APIResponse(responseCode = "409", description = "Conflicto con datos existentes")
    })
    public Response update(
            @Parameter(description = "ID del curso", required = true)
            @PathParam("id") Long id,
            @Parameter(description = "Datos actualizados del curso", required = true)
            @Valid CursoRequestDTO dto) {
        CursoResponseDTO curso = cursoService.update(id, dto);
        return Response.ok(ApiResponse.success("Curso actualizado exitosamente", curso)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar curso", description = "Elimina lógicamente un curso del sistema (solo si no es prerequisito de otros cursos)")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Curso eliminado exitosamente"),
        @APIResponse(responseCode = "400", description = "El curso no se puede eliminar porque es prerequisito de otros cursos"),
        @APIResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public Response delete(
            @Parameter(description = "ID del curso", required = true)
            @PathParam("id") Long id) {
        cursoService.delete(id);
        return Response.ok(ApiResponse.success("Curso eliminado exitosamente")).build();
    }
}
