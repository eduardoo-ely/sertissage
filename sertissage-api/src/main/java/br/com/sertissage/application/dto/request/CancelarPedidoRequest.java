package br.com.sertissage.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelarPedidoRequest {

    @NotBlank(message = "Motivo do cancelamento é obrigatório")
    private String motivo;
}