package br.com.sertissage.application.dto.response;

import br.com.sertissage.domain.entity.PedidoItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PedidoItemResponse {

    private UUID id;
    private UUID materialId;
    private String materialNome;
    private String unidadeMedida;
    private BigDecimal pesoGramas;
    private String observacao;

    public static PedidoItemResponse de(PedidoItem i) {
        PedidoItemResponse r = new PedidoItemResponse();
        r.id = i.getId();
        r.materialId = i.getMaterial().getId();
        r.materialNome = i.getMaterial().getNome();
        r.unidadeMedida = i.getMaterial().getUnidadeMedida();
        r.pesoGramas = i.getPesoGramas();
        r.observacao = i.getObservacao();
        return r;
    }
}