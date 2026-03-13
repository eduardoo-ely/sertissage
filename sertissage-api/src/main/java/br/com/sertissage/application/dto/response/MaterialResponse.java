package br.com.sertissage.application.dto.response;

import br.com.sertissage.domain.entity.Material;
import lombok.Data;

import java.util.UUID;

@Data
public class MaterialResponse {

    private UUID id;
    private String nome;
    private String observacao;
    private String unidadeMedida;
    private Boolean ativo;
    private boolean global;
    private String categoriaNome;
    private String categoriaTipo;

    public static MaterialResponse de(Material m) {
        MaterialResponse r = new MaterialResponse();
        r.id = m.getId();
        r.nome = m.getNome();
        r.observacao = m.getObservacao();
        r.unidadeMedida = m.getUnidadeMedida();
        r.ativo = m.getAtivo();
        r.global = m.isGlobal();
        r.categoriaNome = m.getCategoria().getNome();
        r.categoriaTipo = m.getCategoria().getTipo().name();
        return r;
    }
}