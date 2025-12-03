package upeu.edu.pe.academic.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import upeu.edu.pe.shared.entities.AuditableEntity;
import upeu.edu.pe.shared.listeners.AuditListener;
import upeu.edu.pe.shared.annotations.Normalize;

@Entity
@Table(name = "universidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditListener.class)
public class Universidad extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false, length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 255)
    @Normalize(Normalize.NormalizeType.TITLE_CASE)
    private String nombre;

    @Column(name = "dominio", unique = true, length = 50)
    @Normalize(Normalize.NormalizeType.LOWERCASE)
    private String dominio;

    @Column(name = "ruc", unique = true, nullable = false, length = 11)
    private String ruc; // RUC para Perú (identificación tributaria)

    @Column(name = "tipo", nullable = false, length = 50)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String tipo; // PUBLICA, PRIVADA, CONSORCIO

    @Column(name = "website", length = 255)
    @Normalize(Normalize.NormalizeType.LOWERCASE)
    private String website; // URL oficial: https://upeu.edu.pe

    @Column(name = "logo_url", length = 500)
    private String logoUrl; // URL del logo institucional

    @Column(name = "zona_horaria", length = 50)
    private String zonaHoraria;

    @Column(name = "locale", length = 20)
    private String locale; 

    @Column(name = "configuracion", columnDefinition = "jsonb") 
    private String configuracion;

    
    @Column(name = "plan", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String plan; // FREE, BASIC, PREMIUM, ENTERPRISE

    @Column(name = "estado", length = 20)
    @Normalize(Normalize.NormalizeType.UPPERCASE)
    private String estado; // ACTIVA, SUSPENDIDA, TRIAL, VENCIDA

    @Column(name = "fecha_vencimiento")
    private java.time.LocalDate fechaVencimiento;

    @Column(name = "max_estudiantes")
    private Integer maxEstudiantes; // Límite según el plan

    @Column(name = "max_docentes")
    private Integer maxDocentes; // Límite según el plan

    @Column(name = "total_estudiantes")
    private Integer totalEstudiantes = 0; // Contador actual

    @Column(name = "total_docentes")
    private Integer totalDocentes = 0; // Contador actual

    

    /**
     * Valida si la universidad está activa y su suscripción vigente.
     * 
     * @return true si está activa y no ha vencido la suscripción
     */
    public boolean estaActiva() {
        if (!"ACTIVA".equals(this.estado)) {
            return false;
        }
        if (fechaVencimiento != null && java.time.LocalDate.now().isAfter(fechaVencimiento)) {
            return false;
        }
        return true;
    }

    /**
     * Verifica si la universidad ha excedido el límite de estudiantes.
     * 
     * @return true si alcanzó o superó el límite configurado
     */
    public boolean haExcedidoLimiteEstudiantes() {
        return maxEstudiantes != null && totalEstudiantes != null && totalEstudiantes >= maxEstudiantes;
    }

    /**
     * Verifica si la universidad ha excedido el límite de docentes.
     * 
     * @return true si alcanzó o superó el límite configurado
     */
    public boolean haExcedidoLimiteDocentes() {
        return maxDocentes != null && totalDocentes != null && totalDocentes >= maxDocentes;
    }
    
    /**
     * Suspende la universidad (cambia estado a SUSPENDIDA).
     * Puede usarse por falta de pago o violación de términos.
     */
    public void suspender() {
        this.estado = "SUSPENDIDA";
    }
    
    /**
     * Reactiva la universidad (cambia estado a ACTIVA).
     * Solo si la suscripción no ha vencido.
     */
    public void activar() {
        if (fechaVencimiento != null && java.time.LocalDate.now().isAfter(fechaVencimiento)) {
            throw new IllegalStateException("No se puede activar: suscripción vencida el " + fechaVencimiento);
        }
        this.estado = "ACTIVA";
    }
    
    /**
     * Valida el código de universidad según reglas de negocio.
     * 
     * @param codigo Código a validar
     * @throws IllegalArgumentException si el código es inválido
     */
    public static void validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código no puede estar vacío");
        }
        if (codigo.length() > 20) {
            throw new IllegalArgumentException("Código no puede exceder 20 caracteres");
        }
        if (!codigo.matches("^[A-Z0-9-]+$")) {
            throw new IllegalArgumentException("Código solo puede contener letras mayúsculas, números y guiones");
        }
    }
    
    /**
     * Valida el RUC según formato peruano (11 dígitos).
     * 
     * @param ruc RUC a validar
     * @throws IllegalArgumentException si el RUC es inválido
     */
    public static void validarRuc(String ruc) {
        if (ruc == null || ruc.isBlank()) {
            throw new IllegalArgumentException("RUC no puede estar vacío");
        }
        if (!ruc.matches("^\\d{11}$")) {
            throw new IllegalArgumentException("RUC debe contener exactamente 11 dígitos");
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = "ACTIVA";
        }
        if (this.totalEstudiantes == null) {
            this.totalEstudiantes = 0;
        }
        if (this.totalDocentes == null) {
            this.totalDocentes = 0;
        }
    }
}
