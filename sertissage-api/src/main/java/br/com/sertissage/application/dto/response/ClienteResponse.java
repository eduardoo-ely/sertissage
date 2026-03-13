package br.com.sertissage.application.dto.response;

import br.com.sertissage.domain.entity.Cliente;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ClienteResponse {

    private UUID id;
    private String nome;
    private String telefone;
    private String email;
    private String observacao;
    private LocalDateTime createdAt;

    public static ClienteResponse de(Cliente c) {
        ClienteResponse r = new ClienteResponse();
        r.id = c.getId();
        r.nome = c.getNome();
        r.telefone = c.getTelefone();
        r.email = c.getEmail();
        r.observacao = c.getObservacao();
        r.createdAt = c.getCreatedAt();
        return r;
    }
}