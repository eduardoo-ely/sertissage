package br.com.sertissage.application.dto.response;

import br.com.sertissage.domain.entity.EstoqueMovimentacao;
import br.com.sertissage.domain.enums.OrigemMovimentacao;
import br.com.sertissage.domain.enums.TipoMovimentacao;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MovimentacaoResponse {

    private UUID id;
    private UUID materialId;
    private String materialNome;
    private TipoMovimentacao tipo;
    private OrigemMovimentacao origem;
    private BigDecimal quantidadeGramas;
    private BigDecimal valorPorGrama;
    private BigDecimal valorTotal;
    private String observacao;
    private LocalDateTime createdAt;

    public static MovimentacaoResponse de(EstoqueMovimentacao m) {
        MovimentacaoResponse r = new MovimentacaoResponse();
        r.id = m.getId();
        r.materialId = m.getMaterial().getId();
        r.materialNome = m.getMaterial().getNome();
        r.tipo = m.getTipo();
        r.origem = m.getOrigem();
        r.quantidadeGramas = m.getQuantidadeGramas();
        r.valorPorGrama = m.getValorPorGrama();
        r.valorTotal = m.getValorTotal();
        r.observacao = m.getObservacao();
        r.createdAt = m.getCreatedAt();
        return r;
    }
}