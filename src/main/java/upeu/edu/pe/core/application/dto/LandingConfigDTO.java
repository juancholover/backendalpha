package upeu.edu.pe.core.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO para la configuración de landing page de una universidad.
 * Se almacena como JSON en el campo 'configuracion' de la tabla universidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandingConfigDTO {

    private HeaderConfig header;
    private HeroConfig hero;
    private List<NoticiaConfig> noticias;
    private CampusConfig campus;
    private FooterConfig footer;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HeaderConfig {
        private String logo_url;
        private String nombre_corto;
        private List<LinkConfig> links;
        private CtaButtonConfig cta_button;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HeroConfig {
        private String titulo;
        private String subtitulo;
        private String descripcion;
        private String video_url;
        private List<SlideConfig> slides;
        private List<CtaButtonConfig> cta_buttons;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SlideConfig {
        private String imagen_url;
        private String titulo;
        private String descripcion;
        private Integer duracion;
        private String gradiente;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoticiaConfig {
        private String id;
        private String titulo;
        private String subtitulo;
        private String imagen_url;
        private String link_texto;
        private String link_url;
        private String fecha;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CampusConfig {
        private String titulo;
        private String descripcion;
        private List<SedeConfig> sedes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SedeConfig {
        private String id;
        private String nombre;
        private String ciudad;
        private String direccion;
        private String telefono;
        private String email;
        private String imagen_url;
        private Double latitud;
        private Double longitud;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FooterConfig {
        private String descripcion_corta;
        private List<LinkConfig> links_rapidos;
        private RedesSocialesConfig redes_sociales;
        private ContactoConfig contacto;
        private String copyright;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RedesSocialesConfig {
        private String facebook;
        private String twitter;
        private String instagram;
        private String youtube;
        private String linkedin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContactoConfig {
        private String email;
        private String telefono;
        private String direccion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LinkConfig {
        private String texto;
        private String url;
        private String target;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CtaButtonConfig {
        private String texto;
        private String url;
        private String color;
    }
}
