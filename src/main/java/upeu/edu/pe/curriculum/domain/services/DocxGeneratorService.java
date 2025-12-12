package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.xwpf.usermodel.*;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.entities.SilaboUnidad;
import upeu.edu.pe.curriculum.domain.entities.SilaboActividad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Servicio para generar documentos DOCX de sílabos usando Apache POI.
 */
@ApplicationScoped
public class DocxGeneratorService {

    /**
     * Genera un documento DOCX del sílabo y retorna los bytes.
     */
    public byte[] generarDocxSilabo(Silabo silabo) {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // Título principal
            XWPFParagraph titulo = document.createParagraph();
            titulo.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runTitulo = titulo.createRun();
            runTitulo.setText("SÍLABO");
            runTitulo.setBold(true);
            runTitulo.setFontSize(16);

            // Información del curso
            if (silabo.getCurso() != null) {
                agregarParrafo(document, "Curso: " + silabo.getCurso().getNombre(), true, 12);
                agregarParrafo(document, "Código: " + silabo.getCurso().getCodigoCurso(), false, 11);
                
                if (silabo.getCurso().getHorasSemanales() != null) {
                    agregarParrafo(document, "Horas Semanales: " + silabo.getCurso().getHorasSemanales(), false, 11);
                }
            }

            agregarParrafo(document, "Año Académico: " + silabo.getAnioAcademico(), false, 11);
            document.createParagraph(); // Línea en blanco

            // Sumilla
            if (silabo.getSumilla() != null && !silabo.getSumilla().isEmpty()) {
                agregarSeccion(document, "SUMILLA", silabo.getSumilla());
            }

            // Competencias
            if (silabo.getCompetencias() != null && !silabo.getCompetencias().isEmpty()) {
                agregarSeccion(document, "COMPETENCIAS", silabo.getCompetencias());
            }

            // Metodología
            if (silabo.getMetodologia() != null && !silabo.getMetodologia().isEmpty()) {
                agregarSeccion(document, "METODOLOGÍA", silabo.getMetodologia());
            }

            // Bibliografía
            if (silabo.getBibliografia() != null && !silabo.getBibliografia().isEmpty()) {
                agregarSeccion(document, "BIBLIOGRAFÍA", silabo.getBibliografia());
            }

            // Recursos didácticos
            if (silabo.getRecursosDidacticos() != null && !silabo.getRecursosDidacticos().isEmpty()) {
                agregarSeccion(document, "RECURSOS DIDÁCTICOS", silabo.getRecursosDidacticos());
            }

            // Unidades de aprendizaje
            if (silabo.getUnidades() != null && !silabo.getUnidades().isEmpty()) {
                agregarUnidades(document, silabo);
            }

            // Sistema de evaluación
            agregarSistemaEvaluacion(document, silabo);

            document.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar DOCX del sílabo", e);
        }
    }

    private void agregarParrafo(XWPFDocument document, String texto, boolean bold, int fontSize) {
        XWPFParagraph parrafo = document.createParagraph();
        XWPFRun run = parrafo.createRun();
        run.setText(texto);
        run.setBold(bold);
        run.setFontSize(fontSize);
    }

    private void agregarSeccion(XWPFDocument document, String titulo, String contenido) {
        // Título de sección
        XWPFParagraph parrafoTitulo = document.createParagraph();
        XWPFRun runTitulo = parrafoTitulo.createRun();
        runTitulo.setText(titulo);
        runTitulo.setBold(true);
        runTitulo.setFontSize(12);

        // Contenido
        XWPFParagraph parrafoContenido = document.createParagraph();
        XWPFRun runContenido = parrafoContenido.createRun();
        runContenido.setText(contenido);
        runContenido.setFontSize(11);

        document.createParagraph(); // Línea en blanco
    }

    private void agregarUnidades(XWPFDocument document, Silabo silabo) {
        // Título de sección
        agregarParrafo(document, "UNIDADES DE APRENDIZAJE", true, 12);

        // Crear tabla para unidades
        XWPFTable tabla = document.createTable();
        
        // Encabezados
        XWPFTableRow encabezado = tabla.getRow(0);
        encabezado.getCell(0).setText("Unidad");
        encabezado.addNewTableCell().setText("Título");
        encabezado.addNewTableCell().setText("Semanas");
        encabezado.addNewTableCell().setText("Contenidos");

        // Datos de unidades
        for (SilaboUnidad unidad : silabo.getUnidades()) {
            XWPFTableRow fila = tabla.createRow();
            fila.getCell(0).setText(String.valueOf(unidad.getNumeroUnidad()));
            fila.getCell(1).setText(unidad.getTitulo());
            fila.getCell(2).setText(unidad.getSemanaInicio() + " - " + unidad.getSemanaFin());
            fila.getCell(3).setText(unidad.getContenidos() != null ? unidad.getContenidos() : "");
        }

        document.createParagraph(); // Línea en blanco
    }

    private void agregarSistemaEvaluacion(XWPFDocument document, Silabo silabo) {
        agregarParrafo(document, "SISTEMA DE EVALUACIÓN", true, 12);

        if (silabo.getUnidades() == null || silabo.getUnidades().isEmpty()) {
            return;
        }

        // Crear tabla para actividades
        XWPFTable tabla = document.createTable();
        
        // Encabezados
        XWPFTableRow encabezado = tabla.getRow(0);
        encabezado.getCell(0).setText("Actividad");
        encabezado.addNewTableCell().setText("Tipo");
        encabezado.addNewTableCell().setText("Ponderación");
        encabezado.addNewTableCell().setText("Semana");

        // Recopilar todas las actividades SUMATIVAS
        for (SilaboUnidad unidad : silabo.getUnidades()) {
            if (unidad.getActividades() != null) {
                for (SilaboActividad actividad : unidad.getActividades()) {
                    if ("SUMATIVA".equals(actividad.getTipo())) {
                        XWPFTableRow fila = tabla.createRow();
                        fila.getCell(0).setText(actividad.getNombre());
                        fila.getCell(1).setText(actividad.getTipo());
                        fila.getCell(2).setText(actividad.getPonderacion() != null ? 
                            actividad.getPonderacion().toString() + "%" : "0%");
                        fila.getCell(3).setText(actividad.getSemanaProgramada() != null ? 
                            String.valueOf(actividad.getSemanaProgramada()) : "-");
                    }
                }
            }
        }
    }
}
