package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Universidad;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UniversidadRepository implements PanacheRepositoryBase<Universidad, Long> {

    /**
     * Buscar universidad por RUC
     */
    public Optional<Universidad> findByRuc(String ruc) {
        return find("ruc = ?1 and active = true", ruc).firstResultOptional();
    }

    /**
     * Buscar universidad por sigla
     */
    public Optional<Universidad> findBySigla(String sigla) {
        return find("sigla = ?1 and active = true", sigla).firstResultOptional();
    }

    /**
     * Listar todas las universidades activas
     */
    public List<Universidad> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Buscar universidades por tipo (PUBLICA/PRIVADA)
     */
    public List<Universidad> findByTipo(String tipo) {
        return find("tipo = ?1 and active = true", tipo).list();
    }

    /**
     * Verificar si existe RUC (para validaciones)
     */
    public boolean existsByRuc(String ruc) {
        return count("ruc = ?1 and active = true", ruc) > 0;
    }

    /**
     * Verificar si existe RUC excluyendo un ID específico
     */
    public boolean existsByRucAndIdNot(String ruc, Long id) {
        return count("ruc = ?1 and id != ?2 and active = true", ruc, id) > 0;
    }
}
