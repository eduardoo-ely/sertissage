package br.com.sertissage.domain.entity;

import br.com.sertissage.domain.enums.TipoMovimentacao;
import br.com.sertissage.domain.enums.OrigemMovimentacao;
import java.math.BigDecimal;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "estoque_movimentacao")
public class EstoqueMovimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    @Enumerated(EnumType.STRING)
    private OrigemMovimentacao origem;

    private BigDecimal quantidadeGramas;

    private BigDecimal valorPorGrama;

    private LocalDateTime createdAt = LocalDateTime.now();

    public EstoqueMovimentacao() {}

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
}