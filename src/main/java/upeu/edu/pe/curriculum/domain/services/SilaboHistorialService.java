package upeu.edu.pe.curriculum.domain.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.entities.SilaboHistorial;
import upeu.edu.pe.curriculum.domain.repositories.SilaboHistorialRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar el historial de cambios de los sílabos.
 * Registra automáticamente todos los cambios realizados en un sílabo.
 */
@ApplicationScoped
@Slf4j
public class SilaboHistorialService {

    @Inject
    SilaboHistorialRepository historialRepository;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Registra un cambio en el historial del sílabo
     */
    @Transactional
    public SilaboHistorial registrarCambio(
        Silabo silabo,
        String accion,
        String usuarioEmail,
        String comentarios
    ) {
        SilaboHistorial historial = new SilaboHistorial();
        historial.setSilabo(silabo);
        historial.setVersionAnterior(silabo.getVersion());
        historial.setVersionNueva(silabo.getVersion() + 1);
        historial.setFecha(LocalDateTime.now());
        historial.setAccion(accion);
        historial.setUsuario(usuarioEmail);
        historial.setComentarios(comentarios);

        historialRepository.persist(historial);
        
        log.info("📝 Historial registrado: silabo={}, accion={}, usuario={}", 
            silabo.getId(), accion, usuarioEmail);
        
        return historial;
    }

    /**
     * Registra una modificación con snapshot del contenido anterior
     */
    @Transactional
    public SilaboHistorial registrarModificacion(
        Silabo silaboAnterior,
        Silabo silaboNuevo,
        String usuarioEmail,
        String comentarios
    ) {
        SilaboHistorial historial = new SilaboHistorial();
        historial.setSilabo(silaboNuevo);
        historial.setVersionAnterior(silaboAnterior.getVersion());
        historial.setVersionNueva(silaboNuevo.getVersion());
        historial.setFecha(LocalDateTime.now());
        historial.setAccion("MODIFICACION");
        historial.setUsuario(usuarioEmail);
        historial.setComentarios(comentarios);

        historialRepository.persist(historial);
        
        log.info("📝 Modificación registrada: silabo={}, v{} → v{}, usuario={}", 
            silaboNuevo.getId(), 
            silaboAnterior.getVersion(), 
            silaboNuevo.getVersion(), 
            usuarioEmail);
        
        return historial;
    }

    /**
     * Registra la aprobación de un sílabo
     */
    @Transactional
    public SilaboHistorial registrarAprobacion(
        Silabo silabo,
        String aprobadorEmail,
        String comentarios
    ) {
        return registrarCambio(silabo, "APROBACION", aprobadorEmail, comentarios);
    }

    /**
     * Registra el congelamiento de un sílabo
     */
    @Transactional
    public SilaboHistorial registrarCongelamiento(
        Silabo silabo,
        String usuarioEmail,
        String comentarios
    ) {
        return registrarCambio(silabo, "CONGELAMIENTO", usuarioEmail, comentarios);
    }

    /**
     * Registra la publicación de un sílabo
     */
    @Transactional
    public SilaboHistorial registrarPublicacion(
        Silabo silabo,
        String usuarioEmail,
        String comentarios
    ) {
        return registrarCambio(silabo, "PUBLICACION", usuarioEmail, comentarios);
    }

    /**
     * Registra la creación de un sílabo
     */
    @Transactional
    public SilaboHistorial registrarCreacion(
        Silabo silabo,
        String usuarioEmail
    ) {
        return registrarCambio(silabo, "CREACION", usuarioEmail, "Sílabo creado");
    }

    /**
     * Obtiene todo el historial de un sílabo ordenado por fecha descendente
     */
    public List<SilaboHistorial> obtenerHistorial(Long silaboId) {
        return historialRepository.find(
            "silabo.id = ?1 ORDER BY fecha DESC", 
            silaboId
        ).list();
    }

    /**
     * Obtiene el historial de un sílabo paginado
     */
    public List<SilaboHistorial> obtenerHistorialPaginado(
        Long silaboId, 
        int page, 
        int size
    ) {
        return historialRepository.find(
            "silabo.id = ?1 ORDER BY fecha DESC", 
            silaboId
        ).page(page, size).list();
    }

    /**
     * Obtiene el historial de un sílabo filtrado por acción
     */
    public List<SilaboHistorial> obtenerHistorialPorAccion(
        Long silaboId, 
        String accion
    ) {
        return historialRepository.find(
            "silabo.id = ?1 AND accion = ?2 ORDER BY fecha DESC", 
            silaboId, 
            accion
        ).list();
    }

    /**
     * Obtiene el último cambio de un sílabo
     */
    public SilaboHistorial obtenerUltimoCambio(Long silaboId) {
        return historialRepository.find(
            "silabo.id = ?1 ORDER BY fecha DESC", 
            silaboId
        ).firstResult();
    }

    /**
     * Cuenta los cambios de un sílabo
     */
    public long contarCambios(Long silaboId) {
        return historialRepository.count("silabo.id", silaboId);
    }

    /**
     * Obtiene estadísticas del historial
     */
    public Map<String, Object> obtenerEstadisticas(Long silaboId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalCambios", contarCambios(silaboId));
        stats.put("creaciones", contarPorAccion(silaboId, "CREACION"));
        stats.put("modificaciones", contarPorAccion(silaboId, "MODIFICACION"));
        stats.put("aprobaciones", contarPorAccion(silaboId, "APROBACION"));
        stats.put("congelamientos", contarPorAccion(silaboId, "CONGELAMIENTO"));
        stats.put("publicaciones", contarPorAccion(silaboId, "PUBLICACION"));
        
        SilaboHistorial ultimoCambio = obtenerUltimoCambio(silaboId);
        if (ultimoCambio != null) {
            stats.put("ultimoCambio", ultimoCambio.getFecha());
            stats.put("ultimaAccion", ultimoCambio.getAccion());
            stats.put("ultimoUsuario", ultimoCambio.getUsuario());
        }
        
        return stats;
    }

    /**
     * Cuenta cambios por tipo de acción
     */
    private long contarPorAccion(Long silaboId, String accion) {
        return historialRepository.count(
            "silabo.id = ?1 AND accion = ?2", 
            silaboId, 
            accion
        );
    }
}
