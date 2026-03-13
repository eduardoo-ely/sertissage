package br.com.sertissage.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String telefone;

    @Email(message = "Email inválido")
    private String email;

    private String observacao;
}