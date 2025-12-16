package upeu.edu.pe.shared.infrastructure.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStream;
import java.util.UUID;

/**
 * Servicio para gestión de archivos en Azure Blob Storage.
 * Maneja uploads de logos, imágenes, videos y archivos genéricos.
 */
@ApplicationScoped
@Startup
public class AzureStorageService {

    @ConfigProperty(name = "azure.storage.connection-string")
    String connectionString;

    @ConfigProperty(name = "azure.storage.container-name", defaultValue = "uploads")
    String containerName;

    private BlobServiceClient blobServiceClient;
    private BlobContainerClient containerClient;

    @PostConstruct
    public void init() {
        System.out.println("\n=== INITIALIZING AZURE STORAGE ===");
        System.out.println("Container name: " + containerName);
        System.out.println("Connection string length: " + (connectionString != null ? connectionString.length() : 0));

        try {
            this.blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();

            this.containerClient = blobServiceClient.getBlobContainerClient(containerName);

            // Verificar si el container existe
            if (containerClient.exists()) {
                System.out.println("✅ Azure Storage container existe: " + containerName);
            } else {
                System.out.println("⚠️ Container no existe, intentando crear: " + containerName);
                containerClient.create();
                System.out.println("✅ Azure Storage container creado: " + containerName);
            }

            System.out.println("✅ Azure Storage conectado exitosamente");
            System.out.println("=== AZURE STORAGE READY ===\n");
        } catch (Exception e) {
            System.err.println("❌ Error conectando Azure Storage: " + e.getClass().getSimpleName());
            System.err.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== AZURE STORAGE FAILED ===\n");
            // No lanzar excepción para permitir inicio sin Azure
        }
    }

    /**
     * Sube un archivo a Azure Blob Storage.
     * 
     * @param inputStream Stream del archivo
     * @param fileName    Nombre original del archivo
     * @param contentType Tipo MIME del archivo
     * @param folder      Carpeta destino (logos, landing/hero, etc.)
     * @return URL pública del archivo subido
     */
    public String uploadFile(InputStream inputStream, String fileName, String contentType, String folder) {
        validateConnection();

        // Generar nombre único para evitar colisiones
        String extension = getExtension(fileName);
        String uniqueName = folder + "/" + UUID.randomUUID().toString() + extension;

        BlobClient blobClient = containerClient.getBlobClient(uniqueName);

        // Subir archivo
        blobClient.upload(inputStream, true);

        // Configurar headers para acceso público
        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(contentType);
        blobClient.setHttpHeaders(headers);

        return blobClient.getBlobUrl();
    }

    /**
     * Sube un archivo con nombre específico (para logos de universidad).
     */
    public String uploadFileWithName(InputStream inputStream, String blobName, String contentType) {
        validateConnection();

        BlobClient blobClient = containerClient.getBlobClient(blobName);

        // Subir archivo (sobrescribe si existe)
        blobClient.upload(inputStream, true);

        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(contentType);
        blobClient.setHttpHeaders(headers);

        return blobClient.getBlobUrl();
    }

    /**
     * Sube logo de universidad.
     */
    public String uploadUniversityLogo(InputStream inputStream, Long universidadId, String fileName,
            String contentType) {
        String extension = getExtension(fileName);
        String blobName = "universidades/" + universidadId + "/logo" + extension;
        return uploadFileWithName(inputStream, blobName, contentType);
    }

    /**
     * Sube imagen para landing page.
     */
    public String uploadLandingImage(InputStream inputStream, Long universidadId, String section, String fileName,
            String contentType) {
        String extension = getExtension(fileName);
        String blobName = "universidades/" + universidadId + "/landing/" + section + "/" + UUID.randomUUID()
                + extension;
        return uploadFileWithName(inputStream, blobName, contentType);
    }

    /**
     * Sube video para hero de landing.
     */
    public String uploadHeroVideo(InputStream inputStream, Long universidadId, String fileName, String contentType) {
        String extension = getExtension(fileName);
        String blobName = "universidades/" + universidadId + "/landing/hero/video" + extension;
        return uploadFileWithName(inputStream, blobName, contentType);
    }

    /**
     * Sube imagen de slide del hero.
     */
    public String uploadHeroSlide(InputStream inputStream, Long universidadId, int slideIndex, String fileName,
            String contentType) {
        String extension = getExtension(fileName);
        String blobName = "universidades/" + universidadId + "/landing/hero/slides/slide-" + slideIndex + extension;
        return uploadFileWithName(inputStream, blobName, contentType);
    }

    /**
     * Sube imagen de campus/sede.
     */
    public String uploadCampusImage(InputStream inputStream, Long universidadId, String sedeId, String fileName,
            String contentType) {
        String extension = getExtension(fileName);
        String blobName = "universidades/" + universidadId + "/landing/campus/" + sedeId + extension;
        return uploadFileWithName(inputStream, blobName, contentType);
    }

    /**
     * Sube imagen de configuración del sistema (fondos de portal/login).
     */
    public String uploadConfigImage(InputStream inputStream, Long universidadId, String elementoId, String fileName,
            String contentType) {
        String extension = getExtension(fileName);
        String blobName = "universidades/" + universidadId + "/config/" + elementoId + extension;
        return uploadFileWithName(inputStream, blobName, contentType);
    }

    /**
     * Elimina un archivo de Azure Blob Storage.
     */
    public boolean deleteFile(String blobUrl) {
        validateConnection();

        try {
            // Extraer nombre del blob de la URL
            String blobName = extractBlobName(blobUrl);
            BlobClient blobClient = containerClient.getBlobClient(blobName);

            if (blobClient.exists()) {
                blobClient.delete();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error eliminando archivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si el servicio está disponible.
     */
    public boolean isAvailable() {
        return blobServiceClient != null && containerClient != null;
    }

    private void validateConnection() {
        if (!isAvailable()) {
            throw new IllegalStateException("Azure Storage no está configurado correctamente");
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String extractBlobName(String blobUrl) {
        // URL format: https://account.blob.core.windows.net/container/blobName
        String containerPath = "/" + containerName + "/";
        int index = blobUrl.indexOf(containerPath);
        if (index != -1) {
            return blobUrl.substring(index + containerPath.length());
        }
        return blobUrl;
    }
}
