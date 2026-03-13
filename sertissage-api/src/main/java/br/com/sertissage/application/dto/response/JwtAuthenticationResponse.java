package br.com.sertissage.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtAuthenticationResponse {

    private String token;
    private String tipo = "Bearer";
    private UUID usuarioId;
    private String nome;
    private String email;
    private UUID empresaId;

    public JwtAuthenticationResponse(String token, UUID usuarioId, String nome, 
                                    String email, UUID empresaId) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.email = email;
        this.empresaId = empresaId;
    }
}