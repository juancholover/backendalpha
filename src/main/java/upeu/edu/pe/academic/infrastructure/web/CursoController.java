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
    @Path("/plan/{planId}")
    @Operation(summary = "Listar cursos por plan académico", description = "Obtiene todos los cursos de un plan académico específico")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de cursos obtenida")
    })
    public Response findByPlanAcademico(
            @Parameter(description = "ID del plan académico", required = true)
            @PathParam("planId") Long planId) {
        List<CursoResponseDTO> cursos = cursoService.findByPlanAcademico(planId);
        return Response.ok(ApiResponse.success("Cursos del plan listados", cursos)).build();
    }

    @GET
    @Path("/plan/{planId}/ciclo/{ciclo}")
    @Operation(summary = "Listar cursos por plan y ciclo", description = "Obtiene todos los cursos de un ciclo específico de un plan académico")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de cursos obtenida")
    })
    public Response findByCiclo(
            @Parameter(description = "ID del plan académico", required = true)
            @PathParam("planId") Long planId,
            @Parameter(description = "Número de ciclo", required = true, example = "1")
            @PathParam("ciclo") Integer ciclo) {
        List<CursoResponseDTO> cursos = cursoService.findByCiclo(planId, ciclo);
        return Response.ok(ApiResponse.success("Cursos del ciclo " + ciclo + " listados", cursos)).build();
    }

    @GET
    @Path("/tipo/{tipo}")
    @Operation(summary = "Listar cursos por tipo", description = "Filtra cursos por su tipo (OBLIGATORIO, ELECTIVO, LIBRE)")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de cursos obtenida")
    })
    public Response findByTipoCurso(
            @Parameter(description = "Tipo de curso (OBLIGATORIO, ELECTIVO, LIBRE)", required = true)
            @PathParam("tipo") String tipo) {
        List<CursoResponseDTO> cursos = cursoService.findByTipoCurso(tipo);
        return Response.ok(ApiResponse.success("Cursos filtrados por tipo", cursos)).build();
    }

    @GET
    @Path("/plan/{planId}/sin-prerequisitos")
    @Operation(summary = "Listar cursos sin prerequisitos", description = "Obtiene los cursos que no requieren otros cursos previos (usualmente del primer ciclo)")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Lista de cursos obtenida")
    })
    public Response findCursosSinPrerequisitos(
            @Parameter(description = "ID del plan académico", required = true)
            @PathParam("planId") Long planId) {
        List<CursoResponseDTO> cursos = cursoService.findCursosSinPrerequisitos(planId);
        return Response.ok(ApiResponse.success("Cursos sin prerequisitos listados", cursos)).build();
    }

    @POST
    @Operation(summary = "Crear nuevo curso", description = "Registra un nuevo curso en el sistema asociándolo a un plan académico")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Curso creado exitosamente"),
        @APIResponse(responseCode = "400", description = "Datos de entrada inválidos o regla de negocio violada"),
        @APIResponse(responseCode = "404", description = "Plan académico o prerequisito no encontrado"),
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
