package br.com.sertissage.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = "material",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nome", "empresa_id"})
    }
)
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)   // ← CORRIGIDO (era empresa_id)
    private CategoriaMaterial categoria;

    @Column(nullable = false)
    private String unidadeMedida = "g";

    @Column(nullable = false)
    private Boolean ativo = true;

    // empresa = null → material global (visível para todas as empresas)
    // empresa preenchida → exclusivo da empresa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = true)
    private Empresa empresa;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // Construtor usado pelo SeedData para materiais globais
    public Material(String nome, String observacao,
                    CategoriaMaterial categoria, String unidadeMedida) {
        this.nome = nome;
        this.observacao = observacao;
        this.categoria = categoria;
        this.unidadeMedida = unidadeMedida;
        this.empresa = null;
        this.ativo = true;
    }

    // Helper — true se material pertence ao catálogo global
    public boolean isGlobal() {
        return this.empresa == null;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.unidadeMedida == null) this.unidadeMedida = "g";
        if (this.ativo == null) this.ativo = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}