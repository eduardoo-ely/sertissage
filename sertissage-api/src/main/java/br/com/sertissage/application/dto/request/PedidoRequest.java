package br.com.sertissage.application.dto.request;

import br.com.sertissage.domain.enums.OrigemPedido;
import br.com.sertissage.domain.enums.TipoPedido;
import br.com.sertissage.domain.enums.TipoPeca;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class PedidoRequest {

    @NotNull(message = "Cliente é obrigatório")
    private UUID clienteId;

    @NotNull(message = "Tipo do pedido é obrigatório")
    private TipoPedido tipoPedido;

    private TipoPeca tipoPeca;
    private OrigemPedido origem;
    private String descricao;
    private BigDecimal pesoGramas;
    private BigDecimal custoPorGrama;
    private BigDecimal outrosCustos;
    private BigDecimal precoCobrado;
    private BigDecimal sinal;
    private List<PedidoItemRequest> itens;
}