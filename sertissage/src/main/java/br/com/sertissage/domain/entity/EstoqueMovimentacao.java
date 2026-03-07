package br.com.sertissage.domain.entity;

import br.com.sertissage.domain.enums.OrigemMovimentacao;
import br.com.sertissage.domain.enums.TipoMovimentacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa uma movimentação de estoque.
 *
 * REGRA CRÍTICA (RN04): Esta entidade é imutável após criação.
 * Nenhum campo deve ter setter que permita alteração pós-persistência.
 * Correções são feitas via nova movimentação do tipo AJUSTE (RN09).
 */
@Entity
@Table(name = "estoque_movimentacao")
public class EstoqueMovimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    // Pedido que originou esta movimentação — nulo para entradas manuais e ajustes
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    // Usuário que registrou — rastreabilidade
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    // Origem semântica da movimentação — complementa o tipo
    @Enumerated(EnumType.STRING)
    @Column
    private OrigemMovimentacao origem;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidadeGramas;

    // Valor por grama no momento da movimentação — histórico de custo
    @Column(precision = 10, scale = 2)
    private BigDecimal valorPorGrama;

    // Valor total = quantidadeGramas * valorPorGrama — salvo para histórico
    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public EstoqueMovimentacao() {}

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Apenas Getters — sem Setters para campos pós-criação (imutabilidade RN04)

    public UUID getId() {
        return id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public OrigemMovimentacao getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemMovimentacao origem) {
        this.origem = origem;
    }

    public BigDecimal getQuantidadeGramas() {
        return quantidadeGramas;
    }

    public void setQuantidadeGramas(BigDecimal quantidadeGramas) {
        this.quantidadeGramas = quantidadeGramas;
    }

    public BigDecimal getValorPorGrama() {
        return valorPorGrama;
    }

    public void setValorPorGrama(BigDecimal valorPorGrama) {
        this.valorPorGrama = valorPorGrama;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}