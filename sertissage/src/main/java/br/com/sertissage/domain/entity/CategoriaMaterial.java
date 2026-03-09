package br.com.sertissage.domain.entity;

import br.com.sertissage.domain.enums.TipoCategoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "categoria_material")
public class CategoriaMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Identificador semântico para uso em regras de negócio no Service
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private TipoCategoria tipo;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    // Relacionamento reverso — útil para consultas e relatórios
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Material> materiais = new ArrayList<>();

    public CategoriaMaterial() {}

    public CategoriaMaterial(TipoCategoria tipo, String nome, String descricao) {
        this.tipo = tipo;
        this.nome = nome;
        this.descricao = descricao;
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public TipoCategoria getTipo() {
        return tipo;
    }

    public void setTipo(TipoCategoria tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Material> getMateriais() {
        return materiais;
    }
}