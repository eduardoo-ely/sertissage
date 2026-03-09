package br.com.sertissage.repository;

import br.com.sertissage.domain.entity.CategoriaMaterial;
import br.com.sertissage.domain.enums.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CategoriaMaterialRepository extends JpaRepository<CategoriaMaterial, UUID> {

    Optional<CategoriaMaterial> findByTipo(TipoCategoria tipo);

    boolean existsByTipo(TipoCategoria tipo);
}