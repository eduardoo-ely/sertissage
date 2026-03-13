package br.com.sertissage.domain.repository;

import br.com.sertissage.domain.entity.EstoqueMovimentacao;
import br.com.sertissage.domain.enums.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EstoqueMovimentacaoRepository extends JpaRepository<EstoqueMovimentacao, UUID> {

    @Query("SELECT COALESCE(SUM(" +
           "  CASE WHEN m.tipo = 'ENTRADA' THEN m.quantidadeGramas " +
           "       WHEN m.tipo = 'AJUSTE'  THEN m.quantidadeGramas " +
           "       WHEN m.tipo = 'SAIDA'   THEN -m.quantidadeGramas " +
           "       ELSE 0 END" +
           "), 0) " +
           "FROM EstoqueMovimentacao m " +
           "WHERE m.empresa.id = :empresaId AND m.material.id = :materialId")
    BigDecimal calcularSaldo(
        @Param("empresaId") UUID empresaId,
        @Param("materialId") UUID materialId
    );

    // Histórico de movimentações de um material — ordenado do mais recente
    List<EstoqueMovimentacao> findByEmpresaIdAndMaterialIdOrderByCreatedAtDesc(
        UUID empresaId, UUID materialId
    );

    // Histórico geral da empresa — para auditoria
    List<EstoqueMovimentacao> findByEmpresaIdOrderByCreatedAtDesc(UUID empresaId);

    // Movimentações de um pedido específico
    List<EstoqueMovimentacao> findByPedidoId(UUID pedidoId);

    // Material mais utilizado no período — para relatórios
    @Query("SELECT m.material.id, m.material.nome, COALESCE(SUM(m.quantidadeGramas), 0) as total " +
           "FROM EstoqueMovimentacao m " +
           "WHERE m.empresa.id = :empresaId " +
           "AND m.tipo = 'SAIDA' " +
           "AND m.createdAt BETWEEN :inicio AND :fim " +
           "GROUP BY m.material.id, m.material.nome " +
           "ORDER BY total DESC")
    List<Object[]> findMaterialMaisUtilizado(
        @Param("empresaId") UUID empresaId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );
}