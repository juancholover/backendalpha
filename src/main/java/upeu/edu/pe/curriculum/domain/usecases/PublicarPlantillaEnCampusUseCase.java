package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.*;
import upeu.edu.pe.curriculum.domain.repositories.*;
import upeu.edu.pe.enrollment.domain.repositories.LocalizacionRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Use Case: Publicar una plantilla en múltiples campus.
 * 
 * Workflow:
 * 1. Valida que la plantilla existe y está activa
 * 2. Valida que todos los campus existen
 * 3. Para cada campus:
 *    a. Verifica que no exista publicación activa para ese campus+año
 *    b. Crea una COPIA del sílabo base
 *    c. Crea el registro de publicación
 * 4. Retorna lista de publicaciones creadas
 */
@ApplicationScoped
public class PublicarPlantillaEnCampusUseCase {

    @Inject
    SilaboPlantillaRepository plantillaRepository;

    @Inject
    SilaboPublicacionRepository publicacionRepository;

    @Inject
    SilaboRepository silaboRepository;

    @Inject
    LocalizacionRepository localizacionRepository;

    @Transactional
    public List<SilaboPublicacion> execute(
            Long plantillaId,
            List<Long> localizacionesIds,
            String anioAcademico,
            String publicadoPor,
            LocalDate fechaInicioCampus,
            LocalDate fechaFinCampus) {

        // 1. Validar plantilla
        SilaboPlantilla plantilla = plantillaRepository.findByIdOptional(plantillaId)
                .orElseThrow(() -> new NotFoundException("Plantilla no encontrada con ID: " + plantillaId));

        if (!"ACTIVA".equals(plantilla.getEstado())) {
            throw new BusinessException("La plantilla no está activa. Estado: " + plantilla.getEstado());
        }

        if (!plantilla.estaVigenteEn(LocalDate.now())) {
            throw new BusinessException("La plantilla no está vigente en la fecha actual");
        }

        // 2. Validar año académico
        if (anioAcademico == null || anioAcademico.trim().isEmpty()) {
            throw new BusinessException("El año académico es obligatorio");
        }

        // 3. Validar que hay localizaciones
        if (localizacionesIds == null || localizacionesIds.isEmpty()) {
            throw new BusinessException("Debe especificar al menos un campus para publicar");
        }

        // 4. Publicar en cada campus
        List<SilaboPublicacion> publicaciones = new ArrayList<>();
        Silabo silaboBase = plantilla.getSilabo();

        for (Long localizacionId : localizacionesIds) {
            // 4.1 Validar que el campus existe
            var localizacion = localizacionRepository.findByIdOptional(localizacionId)
                    .orElseThrow(() -> new NotFoundException("Campus no encontrado con ID: " + localizacionId));

            // 4.2 Verificar que no existe publicación activa
            if (publicacionRepository.existsActivaByPlantillaLocalizacionAnio(
                    plantillaId, localizacionId, anioAcademico)) {
                throw new BusinessException(
                    "Ya existe una publicación activa en el campus '" + localizacion.getNombre() + 
                    "' para el año " + anioAcademico
                );
            }

            // 4.3 Crear copia del sílabo para el campus
            Silabo silaboCampus = clonarSilabo(silaboBase, anioAcademico, localizacion.getNombre());
            silaboRepository.persist(silaboCampus);

            // 4.4 Crear publicación
            SilaboPublicacion publicacion = new SilaboPublicacion();
            publicacion.setPlantilla(plantilla);
            publicacion.setLocalizacion(localizacion);
            publicacion.setSilaboCampus(silaboCampus);
            publicacion.setAnioAcademico(anioAcademico);
            publicacion.setEstado("ACTIVA");
            publicacion.setFechaPublicacion(LocalDate.now());
            publicacion.setPublicadoPor(publicadoPor);
            publicacion.setFechaInicioCampus(fechaInicioCampus);
            publicacion.setFechaFinCampus(fechaFinCampus);

            publicacionRepository.persist(publicacion);
            publicaciones.add(publicacion);
        }

        return publicaciones;
    }

    /**
     * Clona un sílabo base para un campus específico.
     */
    private Silabo clonarSilabo(Silabo base, String anioAcademico, String nombreCampus) {
        Silabo clon = new Silabo();
        
        // Copiar datos básicos
        clon.setCurso(base.getCurso());
        clon.setAnioAcademico(anioAcademico);
        clon.setVersion(base.getVersion());
        clon.setEstado("VIGENTE"); // Campus usa versión vigente
        clon.setPorcentajeCalidad(base.getPorcentajeCalidad());
        
        // Copiar contenido académico (inmutable según nivel de flexibilidad)
        clon.setCompetencias(base.getCompetencias());
        clon.setSumilla(base.getSumilla());
        clon.setBibliografia(base.getBibliografia());
        clon.setMetodologia(base.getMetodologia());
        clon.setRecursosDidacticos(base.getRecursosDidacticos());
        
        // Marcar como copia de plantilla
        clon.setObservaciones("Copia de plantilla para campus: " + nombreCampus);
        clon.setEnUso(false); // Campus puede adaptar
        
        // Copiar unidades (sin actividades por ahora - se pueden clonar si se requiere)
        List<SilaboUnidad> unidadesClon = new ArrayList<>();
        for (SilaboUnidad unidadBase : base.getUnidades()) {
            SilaboUnidad unidadClon = clonarUnidad(unidadBase, clon);
            unidadesClon.add(unidadClon);
        }
        clon.setUnidades(unidadesClon);
        
        return clon;
    }

    /**
     * Clona una unidad de sílabo.
     */
    private SilaboUnidad clonarUnidad(SilaboUnidad base, Silabo silaboDestino) {
        SilaboUnidad clon = new SilaboUnidad();
        
        clon.setSilabo(silaboDestino);
        clon.setNumeroUnidad(base.getNumeroUnidad());
        clon.setTitulo(base.getTitulo());
        clon.setSemanaInicio(base.getSemanaInicio());
        clon.setSemanaFin(base.getSemanaFin());
        clon.setContenidos(base.getContenidos());
        clon.setLogroAprendizaje(base.getLogroAprendizaje());
        clon.setEstrategiasEnsenanza(base.getEstrategiasEnsenanza());
        
        // Clonar actividades
        List<SilaboActividad> actividadesClon = new ArrayList<>();
        for (SilaboActividad actividadBase : base.getActividades()) {
            SilaboActividad actividadClon = clonarActividad(actividadBase, clon);
            actividadesClon.add(actividadClon);
        }
        clon.setActividades(actividadesClon);
        
        return clon;
    }

    /**
     * Clona una actividad de sílabo.
     */
    private SilaboActividad clonarActividad(SilaboActividad base, SilaboUnidad unidadDestino) {
        SilaboActividad clon = new SilaboActividad();
        
        clon.setUnidad(unidadDestino);
        clon.setTipo(base.getTipo());
        clon.setNombre(base.getNombre());
        clon.setDescripcion(base.getDescripcion());
        clon.setPonderacion(base.getPonderacion());
        clon.setSemanaProgramada(base.getSemanaProgramada());
        clon.setInstrumentoEvaluacion(base.getInstrumentoEvaluacion());
        clon.setIndicadores(base.getIndicadores());
        
        return clon;
    }
}
