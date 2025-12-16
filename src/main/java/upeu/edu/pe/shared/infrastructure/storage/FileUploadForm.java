package upeu.edu.pe.shared.infrastructure.storage;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

/**
 * DTO para recibir archivos multipart/form-data.
 * Compatible con Quarkus REST (RESTEasy Reactive).
 */
public class FileUploadForm {

    @RestForm("file")
    public FileUpload file;

    @RestForm("fileName")
    public String fileName;

    @RestForm("contentType")
    public String contentType;

    @RestForm("folder")
    public String folder;

    @RestForm("descripcion")
    public String descripcion;
}
