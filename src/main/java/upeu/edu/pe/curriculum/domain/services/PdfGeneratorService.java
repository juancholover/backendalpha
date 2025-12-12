package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.entities.SilaboUnidad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Servicio para generar documentos PDF de sílabos usando Apache PDFBox.
 */
@ApplicationScoped
public class PdfGeneratorService {

    private static final float MARGIN = 50;
    private static final float FONT_SIZE_TITLE = 16;
    private static final float FONT_SIZE_SUBTITLE = 12;
    private static final float FONT_SIZE_BODY = 10;
    private static final float LINE_HEIGHT = 14;

    /**
     * Genera un PDF del sílabo y retorna los bytes.
     */
    public byte[] generarPdfSilabo(Silabo silabo) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = page.getMediaBox().getHeight() - MARGIN;

                // Título principal
                yPosition = escribirTexto(contentStream, "SÍLABO", 
                    MARGIN, yPosition, FONT_SIZE_TITLE, true);
                yPosition -= LINE_HEIGHT;

                // Información del curso
                if (silabo.getCurso() != null) {
                    yPosition = escribirTexto(contentStream, 
                        "Curso: " + silabo.getCurso().getNombre(), 
                        MARGIN, yPosition, FONT_SIZE_SUBTITLE, true);
                    yPosition -= LINE_HEIGHT;

                    yPosition = escribirTexto(contentStream, 
                        "Código: " + silabo.getCurso().getCodigoCurso(), 
                        MARGIN, yPosition, FONT_SIZE_BODY, false);
                    yPosition -= LINE_HEIGHT;

                    if (silabo.getCurso().getHorasSemanales() != null) {
                        yPosition = escribirTexto(contentStream, 
                            "Horas Semanales: " + silabo.getCurso().getHorasSemanales(), 
                            MARGIN, yPosition, FONT_SIZE_BODY, false);
                        yPosition -= LINE_HEIGHT;
                    }
                }

                yPosition -= LINE_HEIGHT;
                yPosition = escribirTexto(contentStream, 
                    "Año Académico: " + silabo.getAnioAcademico(), 
                    MARGIN, yPosition, FONT_SIZE_BODY, false);
                yPosition -= LINE_HEIGHT * 2;

                // Sumilla
                if (silabo.getSumilla() != null && !silabo.getSumilla().isEmpty()) {
                    yPosition = escribirSeccion(contentStream, "SUMILLA", 
                        silabo.getSumilla(), MARGIN, yPosition, page);
                }

                // Competencias
                if (silabo.getCompetencias() != null && !silabo.getCompetencias().isEmpty()) {
                    yPosition = escribirSeccion(contentStream, "COMPETENCIAS", 
                        silabo.getCompetencias(), MARGIN, yPosition, page);
                }

                // Metodología
                if (silabo.getMetodologia() != null && !silabo.getMetodologia().isEmpty()) {
                    yPosition = escribirSeccion(contentStream, "METODOLOGÍA", 
                        silabo.getMetodologia(), MARGIN, yPosition, page);
                }

                // Bibliografía
                if (silabo.getBibliografia() != null && !silabo.getBibliografia().isEmpty()) {
                    yPosition = escribirSeccion(contentStream, "BIBLIOGRAFÍA", 
                        silabo.getBibliografia(), MARGIN, yPosition, page);
                }

                // Recursos didácticos
                if (silabo.getRecursosDidacticos() != null && !silabo.getRecursosDidacticos().isEmpty()) {
                    yPosition = escribirSeccion(contentStream, "RECURSOS DIDÁCTICOS", 
                        silabo.getRecursosDidacticos(), MARGIN, yPosition, page);
                }

                // Unidades
                if (silabo.getUnidades() != null && !silabo.getUnidades().isEmpty()) {
                    yPosition = escribirUnidades(contentStream, silabo, MARGIN, yPosition, page, document);
                }
            }

            document.save(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar PDF del sílabo", e);
        }
    }

    private float escribirTexto(PDPageContentStream contentStream, String texto, 
                                float x, float y, float fontSize, boolean bold) throws IOException {
        contentStream.beginText();
        contentStream.setFont(bold ? 
            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD) : 
            new PDType1Font(Standard14Fonts.FontName.HELVETICA), fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(texto);
        contentStream.endText();
        return y;
    }

    private float escribirSeccion(PDPageContentStream contentStream, String titulo, 
                                  String contenido, float x, float y, PDPage page) throws IOException {
        // Título de sección
        y = escribirTexto(contentStream, titulo, x, y, FONT_SIZE_SUBTITLE, true);
        y -= LINE_HEIGHT;

        // Contenido (puede ser multilínea)
        String[] lineas = contenido.split("\n");
        for (String linea : lineas) {
            if (y < MARGIN + LINE_HEIGHT * 3) {
                // Nueva página si no hay espacio
                break;
            }
            y = escribirTexto(contentStream, linea, x, y, FONT_SIZE_BODY, false);
            y -= LINE_HEIGHT;
        }

        y -= LINE_HEIGHT; // Espacio adicional
        return y;
    }

    private float escribirUnidades(PDPageContentStream contentStream, Silabo silabo, 
                                   float x, float y, PDPage page, PDDocument document) throws IOException {
        y = escribirTexto(contentStream, "UNIDADES DE APRENDIZAJE", x, y, FONT_SIZE_SUBTITLE, true);
        y -= LINE_HEIGHT * 2;

        for (SilaboUnidad unidad : silabo.getUnidades()) {
            if (y < MARGIN + LINE_HEIGHT * 5) {
                break; // No hay más espacio
            }

            y = escribirTexto(contentStream, 
                "Unidad " + unidad.getNumeroUnidad() + ": " + unidad.getTitulo(), 
                x, y, FONT_SIZE_BODY, true);
            y -= LINE_HEIGHT;

            y = escribirTexto(contentStream, 
                "Semanas: " + unidad.getSemanaInicio() + " - " + unidad.getSemanaFin(), 
                x + 10, y, FONT_SIZE_BODY, false);
            y -= LINE_HEIGHT;

            if (unidad.getContenidos() != null) {
                y = escribirTexto(contentStream, 
                    "Contenidos: " + unidad.getContenidos(), 
                    x + 10, y, FONT_SIZE_BODY, false);
                y -= LINE_HEIGHT;
            }

            if (unidad.getLogroAprendizaje() != null) {
                y = escribirTexto(contentStream, 
                    "Logro: " + unidad.getLogroAprendizaje(), 
                    x + 10, y, FONT_SIZE_BODY, false);
                y -= LINE_HEIGHT;
            }

            y -= LINE_HEIGHT;
        }

        return y;
    }
}
