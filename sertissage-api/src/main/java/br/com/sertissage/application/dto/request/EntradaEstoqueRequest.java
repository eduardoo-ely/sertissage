package br.com.sertissage.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class EntradaEstoqueRequest {

    @NotNull(message = "Material é obrigatório")
    private UUID materialId;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    private BigDecimal quantidade;

    private BigDecimal valorPorGrama;
    private String observacao;
}