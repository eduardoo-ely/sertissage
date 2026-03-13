package br.com.sertissage.application.service;

import br.com.sertissage.domain.entity.*;
import br.com.sertissage.domain.enums.OrigemMovimentacao;
import br.com.sertissage.domain.enums.TipoMovimentacao;
import br.com.sertissage.domain.repository.*;
import br.com.sertissage.infrastructure.exception.EstoqueInsuficienteException;
import br.com.sertissage.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueMovimentacaoRepository movimentacaoRepo;
    private final MaterialRepository materialRepo;
    private final EmpresaRepository empresaRepo;

    // ── Consultas ────────────────────────────────────────────────────────────

    /**
     * Retorna o saldo atual de um material para uma empresa.
     * Fórmula: Σ ENTRADAS + Σ AJUSTES − Σ SAIDAS
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldo(UUID empresaId, UUID materialId) {
        return movimentacaoRepo.calcularSaldo(empresaId, materialId);
    }

    /**
     * Retorna o histórico de movimentações de um material, do mais recente ao mais antigo.
     */
    @Transactional(readOnly = true)
    public List<EstoqueMovimentacao> consultarHistorico(UUID empresaId, UUID materialId) {
        // Garante que o material existe e é acessível pela empresa
        buscarMaterialOuErro(materialId, empresaId);
        return movimentacaoRepo.findByEmpresaIdAndMaterialIdOrderByCreatedAtDesc(empresaId, materialId);
    }

    /**
     * Retorna o histórico completo da empresa — usado para auditoria.
     */
    @Transactional(readOnly = true)
    public List<EstoqueMovimentacao> consultarHistoricoCompleto(UUID empresaId) {
        return movimentacaoRepo.findByEmpresaIdOrderByCreatedAtDesc(empresaId);
    }

    // ── Operações de Escrita ─────────────────────────────────────────────────

    /**
     * Registra uma entrada manual de material (ex: compra de ouro).
     * Origem: COMPRA
     */
    @Transactional
    public EstoqueMovimentacao registrarEntrada(UUID empresaId, UUID materialId, UUID usuarioId,
                                                BigDecimal quantidade, BigDecimal valorPorGrama,
                                                String observacao) {
        validarQuantidade(quantidade);

        Empresa empresa = buscarEmpresaOuErro(empresaId);
        Material material = buscarMaterialOuErro(materialId, empresaId);
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId); // referência leve — não carrega o objeto completo

        EstoqueMovimentacao mov = EstoqueMovimentacao.builder()
                .empresa(empresa)
                .material(material)
                .usuario(usuario)
                .tipo(TipoMovimentacao.ENTRADA)
                .origem(OrigemMovimentacao.COMPRA)
                .quantidadeGramas(quantidade)
                .valorPorGrama(valorPorGrama)
                .observacao(observacao)
                .build();

        EstoqueMovimentacao salvo = movimentacaoRepo.save(mov);
        log.info("[Estoque] ENTRADA — material={} | empresa={} | quantidade={}",
                material.getNome(), empresaId, quantidade);
        return salvo;
    }

    /**
     * Registra um ajuste manual de estoque (RN09).
     * Quantidade positiva = aumenta saldo | negativa = reduz saldo.
     * Para ajuste negativo, valida que o saldo não ficará negativo (RN06).
     */
    @Transactional
    public EstoqueMovimentacao registrarAjuste(UUID empresaId, UUID materialId, UUID usuarioId,
                                               BigDecimal quantidade, String observacao) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Quantidade do ajuste não pode ser zero.");
        }

        Empresa empresa = buscarEmpresaOuErro(empresaId);
        Material material = buscarMaterialOuErro(materialId, empresaId);

        // Ajuste negativo: valida que o saldo resultante não ficará negativo (RN06)
        if (quantidade.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal saldoAtual = movimentacaoRepo.calcularSaldo(empresaId, materialId);
            BigDecimal saldoResultante = saldoAtual.add(quantidade); // quantidade já é negativa
            if (saldoResultante.compareTo(BigDecimal.ZERO) < 0) {
                throw new EstoqueInsuficienteException(
                        material.getNome(), saldoAtual, quantidade.abs());
            }
        }

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        // Ajuste negativo é salvo como SAIDA com origem AJUSTE_MANUAL para manter
        // a constraint @Positive em quantidadeGramas
        TipoMovimentacao tipo = quantidade.compareTo(BigDecimal.ZERO) > 0
                ? TipoMovimentacao.AJUSTE
                : TipoMovimentacao.SAIDA;

        EstoqueMovimentacao mov = EstoqueMovimentacao.builder()
                .empresa(empresa)
                .material(material)
                .usuario(usuario)
                .tipo(tipo)
                .origem(OrigemMovimentacao.AJUSTE_MANUAL)
                .quantidadeGramas(quantidade.abs()) // sempre positivo na coluna
                .observacao(observacao)
                .build();

        EstoqueMovimentacao salvo = movimentacaoRepo.save(mov);
        log.info("[Estoque] AJUSTE — material={} | empresa={} | quantidade={}",
                material.getNome(), empresaId, quantidade);
        return salvo;
    }

    /**
     * Gera as saídas de estoque para todos os itens de um pedido.
     * Chamado pelo PedidoService ao mover o pedido para EM_PRODUCAO (RN05).
     *
     * IMPORTANTE: Valida o saldo de TODOS os itens ANTES de salvar qualquer saída.
     * Se qualquer item falhar, nenhuma movimentação é persistida (transação atômica).
     */
    @Transactional
    public void gerarSaidasDoPedido(Pedido pedido, Usuario usuario) {
        List<PedidoItem> itens = pedido.getItens();

        if (itens == null || itens.isEmpty()) {
            log.warn("[Estoque] Pedido {} não possui itens — nenhuma saída gerada.", pedido.getId());
            return;
        }

        UUID empresaId = pedido.getEmpresa().getId();

        // ── Fase 1: Validar saldo de todos os itens ──────────────────────────
        // Lança exceção se QUALQUER item não tiver saldo suficiente.
        // Nenhuma movimentação é salva até que todos passem.
        for (PedidoItem item : itens) {
            BigDecimal saldo = movimentacaoRepo.calcularSaldo(empresaId, item.getMaterial().getId());
            if (saldo.compareTo(item.getPesoGramas()) < 0) {
                throw new EstoqueInsuficienteException(
                        item.getMaterial().getNome(), saldo, item.getPesoGramas());
            }
        }

        // ── Fase 2: Salvar as saídas ─────────────────────────────────────────
        for (PedidoItem item : itens) {
            EstoqueMovimentacao saida = EstoqueMovimentacao.builder()
                    .empresa(pedido.getEmpresa())
                    .material(item.getMaterial())
                    .pedido(pedido)
                    .usuario(usuario)
                    .tipo(TipoMovimentacao.SAIDA)
                    .origem(OrigemMovimentacao.PRODUCAO)
                    .quantidadeGramas(item.getPesoGramas())
                    .observacao("Saída automática — Pedido #" + pedido.getId())
                    .build();

            movimentacaoRepo.save(saida);
        }

        log.info("[Estoque] {} saída(s) gerada(s) para pedido={} | empresa={}",
                itens.size(), pedido.getId(), empresaId);
    }

    // ── Helpers privados ─────────────────────────────────────────────────────

    private Empresa buscarEmpresaOuErro(UUID empresaId) {
        return empresaRepo.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", empresaId));
    }

    private Material buscarMaterialOuErro(UUID materialId, UUID empresaId) {
        return materialRepo.findByIdAndEmpresaIdOrGlobal(materialId, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", materialId));
    }

    private void validarQuantidade(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
    }
}