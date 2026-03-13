package br.com.sertissage.domain.repository;

import br.com.sertissage.domain.entity.Pedido;
import br.com.sertissage.domain.enums.StatusPedido;
import br.com.sertissage.domain.enums.TipoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    List<Pedido> findByEmpresaIdOrderByCreatedAtDesc(UUID empresaId);

    Optional<Pedido> findByIdAndEmpresaId(UUID id, UUID empresaId);
    
    List<Pedido> findByEmpresaIdAndStatusOrderByCreatedAtDesc(UUID empresaId, StatusPedido status);
    
    List<Pedido> findByEmpresaIdAndClienteIdOrderByCreatedAtDesc(UUID empresaId, UUID clienteId);

    // Relatórios

    @Query("SELECT p FROM Pedido p WHERE p.empresa.id = :empresaId " +
           "AND p.status = 'FINALIZADO' " +
           "AND p.updatedAt BETWEEN :inicio AND :fim " +
           "ORDER BY p.updatedAt DESC")
    List<Pedido> findFinalizadosNoPeriodo(
        @Param("empresaId") UUID empresaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    @Query("SELECT COALESCE(SUM(p.margemBruta), 0) FROM Pedido p WHERE p.empresa.id = :empresaId " +
           "AND p.status = 'FINALIZADO' " +
           "AND p.updatedAt BETWEEN :inicio AND :fim")
    BigDecimal sumMargemBrutaFinalizados(
        @Param("empresaId") UUID empresaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    @Query("SELECT COALESCE(AVG(p.percentualMargem), 0) FROM Pedido p WHERE p.empresa.id = :empresaId " +
           "AND p.status = 'FINALIZADO' " +
           "AND p.updatedAt BETWEEN :inicio AND :fim")
    BigDecimal avgPercentualMargemFinalizados(
        @Param("empresaId") UUID empresaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    @Query("SELECT p.tipoPedido, COUNT(p) FROM Pedido p WHERE p.empresa.id = :empresaId " +
           "AND p.status = 'FINALIZADO' " +
           "AND p.updatedAt BETWEEN :inicio AND :fim " +
           "GROUP BY p.tipoPedido")
    List<Object[]> countByTipoPedidoFinalizados(
        @Param("empresaId") UUID empresaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    @Query("SELECT p.origem, COUNT(p) FROM Pedido p WHERE p.empresa.id = :empresaId " +
           "AND p.status = 'FINALIZADO' " +
           "AND p.updatedAt BETWEEN :inicio AND :fim " +
           "GROUP BY p.origem")
    List<Object[]> countByOrigemFinalizados(
        @Param("empresaId") UUID empresaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );
}