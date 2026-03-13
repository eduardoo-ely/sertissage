package br.com.sertissage.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PedidoItemRequest {

    @NotNull(message = "Material é obrigatório")
    private UUID materialId;

    @NotNull(message = "Peso é obrigatório")
    @Positive(message = "Peso deve ser maior que zero")
    private BigDecimal pesoGramas;

    private String observacao;
}