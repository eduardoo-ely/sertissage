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

    // Listagem base por empresa — sempre usar este
    List<Pedido> findByEmpresaIdOrderByCreatedAtDesc(UUID empresaId);

    // Busca por ID com isolamento multiempresa
    Optional<Pedido> findByIdAndEmpresaId(UUID id, UUID empresaId);

    // Listagem por status — usado no painel operacional
    List<Pedido> findByEmpresaIdAndStatusOrderByCreatedAtDesc(UUID empresaId, StatusPedido status);

    // Listagem por cliente
    List<Pedido> findByEmpresaIdAndClienteIdOrderByCreatedAtDesc(UUID empresaId, UUID clienteId);

    // ── Queries para Relatórios (RN08) ──────────────────

    @Query("SELECT p FROM Pedido p WHERE p.empresa.id = :empresaId " +
           "AND p.status = 'FINALIZADO' " +
           "AND p.updatedAt BETWEEN :inicio AND :fim " +
           "ORDER BY p.updatedAt DESC")
    List<Pedido> findFinalizadosNoPeriodo(
        @Param("empresaId") UUID empresaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    /**
     * Lucro total no período — soma das margens brutas de pedidos finalizados.
     */
    @Query("SELECT COALESCE(SUM(p.margemBruta), 0) FROM Pedido p WHERE p.empresa.id = :empresaId " +
           "AND p.status = 'FINALIZADO' " +
           "AND p.updatedAt BETWEEN :inicio AND :fim")
    BigDecimal sumMargemBrutaFinalizados(
        @Param("empresaId") UUID empresaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    /**
     * Margem média no período.
     */
    @Query("SELECT COALESCE(AVG(p.percentualMargem), 0) FROM Pedido p WHERE p.empresa.id = :empresaId " +
           "AND p.status = 'FINALIZADO' " +
           "AND p.updatedAt BETWEEN :inicio AND :fim")
    BigDecimal avgPercentualMargemFinalizados(
        @Param("empresaId") UUID empresaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    /**
     * Contagem por tipo de pedido (FABRICACAO / CONSERTO) no período.
     */
    @Query("SELECT p.tipoPedido, COUNT(p) FROM Pedido p WHERE p.empresa.id = :empresaId " +
           "AND p.status = 'FINALIZADO' " +
           "AND p.updatedAt BETWEEN :inicio AND :fim " +
           "GROUP BY p.tipoPedido")
    List<Object[]> countByTipoPedidoFinalizados(
        @Param("empresaId") UUID empresaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    /**
     * Contagem por origem (INSTAGRAM, LOJA, etc.) no período.
     */
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