package br.com.sertissage.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MaterialRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String observacao;

    @NotBlank(message = "Unidade de medida é obrigatória")
    private String unidadeMedida;

    @NotNull(message = "Categoria é obrigatória")
    private UUID categoriaId;
}