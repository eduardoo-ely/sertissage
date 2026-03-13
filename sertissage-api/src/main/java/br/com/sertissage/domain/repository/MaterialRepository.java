package br.com.sertissage.domain.repository;

import br.com.sertissage.domain.entity.Material;
import br.com.sertissage.domain.enums.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {

    @Query("SELECT m FROM Material m WHERE (m.empresa IS NULL OR m.empresa.id = :empresaId) AND m.ativo = true ORDER BY m.nome")
    List<Material> findDisponiveis(@Param("empresaId") UUID empresaId);

    @Query("SELECT m FROM Material m JOIN m.categoria c WHERE (m.empresa IS NULL OR m.empresa.id = :empresaId) AND c.tipo = :tipoCategoria AND m.ativo = true ORDER BY m.nome")
    List<Material> findDisponiveisPorCategoria(
        @Param("empresaId") UUID empresaId,
        @Param("tipoCategoria") TipoCategoria tipoCategoria
    );

    @Query("SELECT m FROM Material m WHERE m.id = :id AND (m.empresa IS NULL OR m.empresa.id = :empresaId)")
    Optional<Material> findByIdAndEmpresaIdOrGlobal(
        @Param("id") UUID id,
        @Param("empresaId") UUID empresaId
    );

    List<Material> findByEmpresaId(UUID empresaId);

    boolean existsByNomeAndEmpresaId(String nome, UUID empresaId);
}