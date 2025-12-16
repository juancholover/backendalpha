package upeu.edu.pe.core.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.core.application.dto.UniversidadRequestDTO;
import upeu.edu.pe.core.application.dto.UniversidadResponseDTO;
import upeu.edu.pe.core.application.mapper.UniversidadMapper;
import upeu.edu.pe.core.domain.commands.ActualizarUniversidadCommand;
import upeu.edu.pe.core.domain.commands.CrearUniversidadCommand;
import upeu.edu.pe.core.domain.entities.Universidad;
import upeu.edu.pe.core.domain.services.UniversidadService;
import upeu.edu.pe.core.domain.services.LandingConfigService;
import upeu.edu.pe.core.domain.usecases.ActualizarUniversidadUseCase;
import upeu.edu.pe.core.domain.usecases.CrearUniversidadUseCase;
import upeu.edu.pe.core.domain.usecases.EliminarUniversidadUseCase;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.List;

/**
 * Controlador REST para universidades.
 * 
 * Arquitectura:
 * - Operaciones de ESCRITURA delegadas a Use Cases
 * - Operaciones de LECTURA delegadas al Service
 */
@Path("/api/v1/universidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Universidades", description = "Gestión de universidades del sistema académico")
public class UniversidadController {

    // Use Cases para operaciones de escritura
    @Inject
    CrearUniversidadUseCase crearUseCase;

    @Inject
    ActualizarUniversidadUseCase actualizarUseCase;

    @Inject
    EliminarUniversidadUseCase eliminarUseCase;

    // Service para operaciones de lectura
    @Inject
    UniversidadService universidadService;

    @Inject
    UniversidadMapper universidadMapper;

    @Inject
    LandingConfigService landingConfigService;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    @Operation(summary = "Listar universidades")
    @APIResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public Response findAll() {
        List<UniversidadResponseDTO> universidades = universidadService.findAll();
        return Response.ok(ApiResponse.success("Universidades obtenidas", universidades)).build();
    }

    @GET
    @Path("/activas")
    @Operation(summary = "Listar universidades activas")
    public Response findAllActive() {
        List<UniversidadResponseDTO> universidades = universidadService.findAllActive();
        return Response.ok(ApiResponse.success("Universidades activas obtenidas", universidades)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar universidad por ID")
    public Response findById(@PathParam("id") Long id) {
        UniversidadResponseDTO universidad = universidadService.findById(id);
        return Response.ok(ApiResponse.success("Universidad encontrada", universidad)).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    @Operation(summary = "Buscar universidad por código")
    public Response findByCodigo(@PathParam("codigo") String codigo) {
        UniversidadResponseDTO universidad = universidadService.findByCodigo(codigo);
        return Response.ok(ApiResponse.success("Universidad encontrada", universidad)).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Buscar universidades")
    public Response search(@QueryParam("q") String query) {
        List<UniversidadResponseDTO> universidades = universidadService.search(query);
        return Response.ok(ApiResponse.success("Resultados de búsqueda", universidades)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    @Operation(summary = "Crear universidad")
    @APIResponse(responseCode = "201", description = "Universidad creada exitosamente")
    public Response create(@Valid UniversidadRequestDTO dto) {
        CrearUniversidadCommand command = new CrearUniversidadCommand(
                dto.getCodigo(),
                dto.getNombre(),
                dto.getRuc(),
                dto.getTipo(),
                dto.getPlan(),
                dto.getDominio(),
                dto.getWebsite(),
                dto.getMaxEstudiantes(),
                dto.getMaxDocentes());

        Universidad universidad = crearUseCase.execute(command);
        UniversidadResponseDTO response = universidadMapper.toResponseDTO(universidad);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Universidad creada", response))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar universidad")
    public Response update(@PathParam("id") Long id, @Valid UniversidadRequestDTO dto) {
        ActualizarUniversidadCommand command = new ActualizarUniversidadCommand(
                id,
                dto.getNombre(),
                dto.getTipo(),
                dto.getPlan(),
                dto.getDominio(),
                dto.getWebsite(),
                dto.getEstado(),
                dto.getMaxEstudiantes(),
                dto.getMaxDocentes());

        Universidad universidad = actualizarUseCase.execute(command);
        UniversidadResponseDTO response = universidadMapper.toResponseDTO(universidad);

        return Response.ok(ApiResponse.success("Universidad actualizada", response)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar universidad")
    public Response delete(@PathParam("id") Long id) {
        eliminarUseCase.execute(id);
        return Response.ok(ApiResponse.success("Universidad eliminada", null)).build();
    }

    // =====================================================
    // OPERACIONES DE MULTIMEDIA
    // =====================================================

    @Inject
    upeu.edu.pe.shared.infrastructure.storage.AzureStorageService storageService;

    @PUT
    @Path("/{id}/logo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Subir logo de universidad")
    public Response uploadLogo(
            @PathParam("id") Long id,
            @org.jboss.resteasy.reactive.RestForm("file") org.jboss.resteasy.reactive.multipart.FileUpload file,
            @org.jboss.resteasy.reactive.RestForm("fileName") String fileName,
            @org.jboss.resteasy.reactive.RestForm("contentType") String contentType) {
        System.out.println("📥 [UniversidadController] Recibiendo logo para universidad: " + id);
        System.out.println("   File: " + (file != null ? file.fileName() : "null"));

        try {
            if (!storageService.isAvailable()) {
                System.err.println("ERROR: Azure Storage no disponible");
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(ApiResponse.error("Azure Storage no disponible"))
                        .build();
            }
            if (file == null) {
                System.err.println("ERROR: No se recibió archivo");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Archivo requerido"))
                        .build();
            }

            String actualFileName = fileName != null && !fileName.equals("string") ? fileName : file.fileName();
            String actualContentType = contentType != null && !contentType.equals("string") ? contentType : "image/png";

            String url = storageService.uploadUniversityLogo(
                    new java.io.FileInputStream(file.uploadedFile().toFile()),
                    id,
                    actualFileName,
                    actualContentType);

            // Actualizar logo_url en la BD
            universidadService.updateLogoUrl(id, url);

            System.out.println("✅ Logo subido exitosamente: " + url);
            return Response.ok(ApiResponse.success("Logo subido", java.util.Map.of("logoUrl", url))).build();
        } catch (Exception e) {
            System.err.println("❌ Error subiendo logo: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error: " + e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}/configuracion/{elementoId}/imagen")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Subir imagen de configuración del sistema")
    public Response uploadConfigImage(
            @PathParam("id") Long id,
            @PathParam("elementoId") String elementoId,
            @org.jboss.resteasy.reactive.RestForm("file") org.jboss.resteasy.reactive.multipart.FileUpload file,
            @org.jboss.resteasy.reactive.RestForm("fileName") String fileName,
            @org.jboss.resteasy.reactive.RestForm("contentType") String contentType) {
        System.out.println(
                "📥 [UniversidadController] Recibiendo imagen config " + elementoId + " para universidad: " + id);

        try {
            if (!storageService.isAvailable()) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(ApiResponse.error("Azure Storage no disponible"))
                        .build();
            }
            if (file == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Archivo requerido"))
                        .build();
            }

            String actualFileName = fileName != null && !fileName.equals("string") ? fileName : file.fileName();
            String actualContentType = contentType != null && !contentType.equals("string") ? contentType
                    : "image/jpeg";

            String url = storageService.uploadConfigImage(
                    new java.io.FileInputStream(file.uploadedFile().toFile()),
                    id,
                    elementoId,
                    actualFileName,
                    actualContentType);

            // Actualizar URL en la configuración de la BD
            landingConfigService.updateSistemaElementUrl(id, elementoId, url);

            System.out.println("✅ Imagen config subida y guardada en BD: " + url);
            return Response.ok(ApiResponse.success("Imagen de configuración subida", java.util.Map.of("url", url)))
                    .build();
        } catch (Exception e) {
            System.err.println("❌ Error subiendo imagen config: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error: " + e.getMessage()))
                    .build();
        }
    }
}
