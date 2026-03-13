package br.com.sertissage.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AjusteEstoqueRequest {

    @NotNull(message = "Material é obrigatório")
    private UUID materialId;

    @NotNull(message = "Quantidade é obrigatória")
    private BigDecimal quantidade;

    private String observacao;
}