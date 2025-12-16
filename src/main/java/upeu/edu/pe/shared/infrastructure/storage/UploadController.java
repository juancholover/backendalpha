package upeu.edu.pe.shared.infrastructure.storage;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import upeu.edu.pe.shared.response.ApiResponse;

import java.io.FileInputStream;
import java.util.Map;

/**
 * Controlador REST para subida de archivos a Azure Storage.
 */
@Path("/api/v1")
@Tag(name = "Uploads", description = "Subida de archivos a Azure Storage")
public class UploadController {

    @Inject
    AzureStorageService storageService;

    /**
     * Subida genérica de archivos.
     */
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Subir archivo genérico")
    public Response uploadFile(
            @RestForm("file") FileUpload file,
            @RestForm("fileName") String fileName,
            @RestForm("contentType") String contentType,
            @RestForm("folder") String folder) {
        try {
            if (!storageService.isAvailable()) {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                        .entity(ApiResponse.error("Azure Storage no está disponible"))
                        .build();
            }
            if (file == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Archivo requerido"))
                        .build();
            }

            String actualFileName = fileName != null ? fileName : file.fileName();
            String actualContentType = contentType != null ? contentType : file.contentType();
            String actualFolder = folder != null ? folder : "uploads";

            String url = storageService.uploadFile(
                    new FileInputStream(file.uploadedFile().toFile()),
                    actualFileName,
                    actualContentType,
                    actualFolder);

            return Response.ok(ApiResponse.success("Archivo subido correctamente", Map.of(
                    "url", url,
                    "filename", actualFileName))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error subiendo archivo", e.getMessage()))
                    .build();
        }
    }

    // NOTA: El endpoint uploadUniversityLogo (/universidades/{id}/logo)
    // fue movido a UniversidadController para evitar conflictos de routing.

    /**
     * Subir logo para header de landing page.
     */
    @PUT
    @Path("/universidades/{id}/landing/header/logo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Subir logo del header de landing")
    public Response uploadHeaderLogo(
            @PathParam("id") Long universidadId,
            @RestForm("file") FileUpload file,
            @RestForm("fileName") String fileName,
            @RestForm("contentType") String contentType) {
        try {
            String actualFileName = fileName != null ? fileName : file.fileName();
            String actualContentType = contentType != null ? contentType : "image/png";

            String url = storageService.uploadLandingImage(
                    new FileInputStream(file.uploadedFile().toFile()),
                    universidadId,
                    "header",
                    actualFileName,
                    actualContentType);

            return Response.ok(ApiResponse.success("Logo del header subido", Map.of("url", url))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error subiendo logo del header", e.getMessage()))
                    .build();
        }
    }

    /**
     * Subir video del hero de landing page.
     */
    @PUT
    @Path("/universidades/{id}/landing/hero/video")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Subir video del hero")
    public Response uploadHeroVideo(
            @PathParam("id") Long universidadId,
            @RestForm("file") FileUpload file,
            @RestForm("fileName") String fileName,
            @RestForm("contentType") String contentType) {
        try {
            String actualFileName = fileName != null ? fileName : file.fileName();
            String actualContentType = contentType != null ? contentType : "video/mp4";

            String url = storageService.uploadHeroVideo(
                    new FileInputStream(file.uploadedFile().toFile()),
                    universidadId,
                    actualFileName,
                    actualContentType);

            return Response.ok(ApiResponse.success("Video del hero subido", Map.of("url", url))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error subiendo video", e.getMessage()))
                    .build();
        }
    }

    /**
     * Subir imagen de slide del hero.
     */
    @PUT
    @Path("/universidades/{id}/landing/hero/slide/{index}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Subir imagen de slide del hero")
    public Response uploadHeroSlide(
            @PathParam("id") Long universidadId,
            @PathParam("index") Integer slideIndex,
            @RestForm("file") FileUpload file,
            @RestForm("fileName") String fileName,
            @RestForm("contentType") String contentType) {
        try {
            String actualFileName = fileName != null ? fileName : file.fileName();
            String actualContentType = contentType != null ? contentType : "image/jpeg";

            String url = storageService.uploadHeroSlide(
                    new FileInputStream(file.uploadedFile().toFile()),
                    universidadId,
                    slideIndex,
                    actualFileName,
                    actualContentType);

            return Response.ok(ApiResponse.success("Slide subido", Map.of("url", url))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error subiendo slide", e.getMessage()))
                    .build();
        }
    }

    /**
     * Subir imagen de campus/sede.
     */
    @PUT
    @Path("/universidades/{id}/landing/sede/{sedeId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Subir imagen de sede/campus")
    public Response uploadSedeImage(
            @PathParam("id") Long universidadId,
            @PathParam("sedeId") String sedeId,
            @RestForm("file") FileUpload file,
            @RestForm("fileName") String fileName,
            @RestForm("contentType") String contentType) {
        try {
            String actualFileName = fileName != null ? fileName : file.fileName();
            String actualContentType = contentType != null ? contentType : "image/jpeg";

            String url = storageService.uploadCampusImage(
                    new FileInputStream(file.uploadedFile().toFile()),
                    universidadId,
                    sedeId,
                    actualFileName,
                    actualContentType);

            return Response.ok(ApiResponse.success("Imagen de sede subida", Map.of("url", url))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error subiendo imagen de sede", e.getMessage()))
                    .build();
        }
    }

    // NOTA: El endpoint uploadConfigImage
    // (/universidades/{id}/configuracion/{elementoId}/imagen)
    // fue movido a UniversidadController para evitar conflictos de routing.
}
