package upeu.edu.pe.core.domain.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.application.dto.LandingConfigDTO;
import upeu.edu.pe.core.domain.entities.Universidad;
import upeu.edu.pe.core.domain.repositories.UniversidadRepository;

import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestión de configuración de landing page.
 */
@ApplicationScoped
public class LandingConfigService {

    @Inject
    UniversidadRepository universidadRepository;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Obtiene la configuración completa de landing de una universidad.
     */
    public LandingConfigDTO getLandingConfig(Long universidadId) {
        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new IllegalArgumentException("Universidad no encontrada: " + universidadId));

        return parseConfig(universidad.getConfiguracion());
    }

    /**
     * Obtiene la configuración de la primera universidad activa (para endpoints
     * públicos).
     */
    public LandingConfigDTO getDefaultLandingConfig() {
        Optional<Universidad> universidad = universidadRepository.findFirstActive();
        return universidad.map(u -> parseConfig(u.getConfiguracion()))
                .orElse(getDefaultConfig());
    }

    /**
     * Obtiene la configuración del sistema (fondos, logos).
     * Lee la sección "sistema" del JSON de configuración.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSistemaConfig() {
        try {
            Optional<Universidad> universidad = universidadRepository.findFirstActive();
            if (universidad.isEmpty()) {
                return Map.of();
            }

            String json = universidad.get().getConfiguracion();
            if (json == null || json.isBlank()) {
                return Map.of();
            }

            // Parsear JSON completo a Map
            Map<String, Object> fullConfig = objectMapper.readValue(json,
                    new TypeReference<Map<String, Object>>() {
                    });

            // Retornar solo la sección "sistema"
            Object sistemaConfig = fullConfig.get("sistema");
            if (sistemaConfig instanceof Map) {
                return (Map<String, Object>) sistemaConfig;
            }

            return Map.of();
        } catch (Exception e) {
            System.err.println("Error obteniendo configuración del sistema: " + e.getMessage());
            return Map.of();
        }
    }

    /**
     * Actualiza la URL de un elemento específico de configuración del sistema.
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public void updateSistemaElementUrl(Long universidadId, String elementoId, String url) {
        try {
            Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                    .orElseThrow(() -> new IllegalArgumentException("Universidad no encontrada: " + universidadId));

            String json = universidad.getConfiguracion();
            Map<String, Object> fullConfig;

            if (json == null || json.isBlank()) {
                // Crear configuración básica si no existe
                fullConfig = new java.util.HashMap<>();
                fullConfig.put("landing", Map.of());
                fullConfig.put("sistema", new java.util.HashMap<>());
            } else {
                fullConfig = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
                });
            }

            // Obtener o crear la sección sistema
            Map<String, Object> sistemaConfig;
            if (fullConfig.get("sistema") instanceof Map) {
                sistemaConfig = new java.util.HashMap<>((Map<String, Object>) fullConfig.get("sistema"));
            } else {
                sistemaConfig = new java.util.HashMap<>();
            }

            // Obtener o crear el elemento
            Map<String, Object> elemento;
            if (sistemaConfig.get(elementoId) instanceof Map) {
                elemento = new java.util.HashMap<>((Map<String, Object>) sistemaConfig.get(elementoId));
            } else {
                elemento = new java.util.HashMap<>();
                elemento.put("id", elementoId);
                elemento.put("descripcion", "Elemento: " + elementoId);
            }

            // Actualizar URL
            elemento.put("url", url);
            sistemaConfig.put(elementoId, elemento);
            fullConfig.put("sistema", sistemaConfig);

            // Guardar
            String newJson = objectMapper.writeValueAsString(fullConfig);
            universidad.setConfiguracion(newJson);
            universidadRepository.persist(universidad);

            System.out.println("✅ URL actualizada para elemento " + elementoId + ": " + url);
        } catch (Exception e) {
            System.err.println("Error actualizando URL del elemento " + elementoId + ": " + e.getMessage());
            throw new RuntimeException("Error actualizando configuración del sistema", e);
        }
    }

    /**
     * Actualiza la configuración completa de landing.
     */
    @Transactional
    public LandingConfigDTO updateLandingConfig(Long universidadId, LandingConfigDTO config) {
        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new IllegalArgumentException("Universidad no encontrada: " + universidadId));

        universidad.setConfiguracion(toJson(config));
        universidadRepository.persist(universidad);
        return config;
    }

    /**
     * Actualiza solo el header de landing.
     */
    @Transactional
    public LandingConfigDTO.HeaderConfig updateHeader(Long universidadId, LandingConfigDTO.HeaderConfig header) {
        LandingConfigDTO config = getLandingConfig(universidadId);
        config.setHeader(header);
        updateLandingConfig(universidadId, config);
        return header;
    }

    /**
     * Actualiza solo el hero de landing.
     */
    @Transactional
    public LandingConfigDTO.HeroConfig updateHero(Long universidadId, LandingConfigDTO.HeroConfig hero) {
        LandingConfigDTO config = getLandingConfig(universidadId);
        config.setHero(hero);
        updateLandingConfig(universidadId, config);
        return hero;
    }

    /**
     * Actualiza solo el campus de landing.
     */
    @Transactional
    public LandingConfigDTO.CampusConfig updateCampus(Long universidadId, LandingConfigDTO.CampusConfig campus) {
        LandingConfigDTO config = getLandingConfig(universidadId);
        config.setCampus(campus);
        updateLandingConfig(universidadId, config);
        return campus;
    }

    /**
     * Actualiza solo el footer de landing.
     */
    @Transactional
    public LandingConfigDTO.FooterConfig updateFooter(Long universidadId, LandingConfigDTO.FooterConfig footer) {
        LandingConfigDTO config = getLandingConfig(universidadId);
        config.setFooter(footer);
        updateLandingConfig(universidadId, config);
        return footer;
    }

    /**
     * Parsea JSON a LandingConfigDTO.
     */
    private LandingConfigDTO parseConfig(String json) {
        if (json == null || json.isBlank()) {
            return getDefaultConfig();
        }
        try {
            return objectMapper.readValue(json, LandingConfigDTO.class);
        } catch (JsonProcessingException e) {
            System.err.println("Error parseando configuración de landing: " + e.getMessage());
            return getDefaultConfig();
        }
    }

    /**
     * Convierte LandingConfigDTO a JSON.
     */
    private String toJson(LandingConfigDTO config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando configuración de landing", e);
        }
    }

    /**
     * Configuración por defecto cuando no existe.
     */
    private LandingConfigDTO getDefaultConfig() {
        return LandingConfigDTO.builder()
                .header(LandingConfigDTO.HeaderConfig.builder()
                        .nombre_corto("Universidad")
                        .build())
                .hero(LandingConfigDTO.HeroConfig.builder()
                        .titulo("Bienvenido")
                        .subtitulo("Sistema Académico")
                        .build())
                .footer(LandingConfigDTO.FooterConfig.builder()
                        .copyright("© 2025 Todos los derechos reservados")
                        .build())
                .build();
    }
}
