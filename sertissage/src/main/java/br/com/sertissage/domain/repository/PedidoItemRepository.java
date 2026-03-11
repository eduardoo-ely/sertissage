package br.com.sertissage.domain.repository;

import br.com.sertissage.domain.entity.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoItemRepository extends JpaRepository<PedidoItem, UUID> {

    // Itens de um pedido — usado pelo EstoqueService ao gerar saídas
    List<PedidoItem> findByPedidoId(UUID pedidoId);

    // Verifica se algum pedido usa determinado material
    boolean existsByMaterialId(UUID materialId);
}