package br.com.sertissage.application.dto.response;

import br.com.sertissage.domain.entity.Pedido;
import br.com.sertissage.domain.enums.OrigemPedido;
import br.com.sertissage.domain.enums.StatusPedido;
import br.com.sertissage.domain.enums.TipoPedido;
import br.com.sertissage.domain.enums.TipoPeca;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class PedidoResponse {

    private UUID id;
    private String descricao;
    private TipoPedido tipoPedido;
    private TipoPeca tipoPeca;
    private OrigemPedido origem;
    private StatusPedido status;
    private BigDecimal pesoGramas;
    private BigDecimal custoPorGrama;
    private BigDecimal outrosCustos;
    private BigDecimal precoCobrado;
    private BigDecimal margemBruta;
    private BigDecimal percentualMargem;
    private BigDecimal sinal;
    private UUID clienteId;
    private String clienteNome;
    private List<PedidoItemResponse> itens;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PedidoResponse de(Pedido p) {
        PedidoResponse r = new PedidoResponse();
        r.id = p.getId();
        r.descricao = p.getDescricao();
        r.tipoPedido = p.getTipoPedido();
        r.tipoPeca = p.getTipoPeca();
        r.origem = p.getOrigem();
        r.status = p.getStatus();
        r.pesoGramas = p.getPesoGramas();
        r.custoPorGrama = p.getCustoPorGrama();
        r.outrosCustos = p.getOutrosCustos();
        r.precoCobrado = p.getPrecoCobrado();
        r.margemBruta = p.getMargemBruta();
        r.percentualMargem = p.getPercentualMargem();
        r.sinal = p.getSinal();
        r.clienteId = p.getCliente().getId();
        r.clienteNome = p.getCliente().getNome();
        r.itens = p.getItens().stream()
                .map(PedidoItemResponse::de)
                .collect(Collectors.toList());
        r.createdAt = p.getCreatedAt();
        r.updatedAt = p.getUpdatedAt();
        return r;
    }
}