package br.com.sertissage.domain.entity;

import br.com.sertissage.domain.enums.OrigemPedido;
import br.com.sertissage.domain.enums.StatusPedido;
import br.com.sertissage.domain.enums.TipoPedido;
import br.com.sertissage.domain.enums.TipoPeca;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Descrição livre do pedido — ex: "Anel de formatura tamanho 18"
    @Column(columnDefinition = "TEXT")
    private String descricao;

    // FABRICACAO ou CONSERTO — define regras de negócio (ex: peso obrigatório só para FABRICACAO)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPedido tipoPedido;

    // Categoria da peça — ex: ANEL, CORRENTE, PINGENTE
    @Enumerated(EnumType.STRING)
    @Column
    private TipoPeca tipoPeca;

    // Canal de origem — usado nos relatórios e futuramente no CRM
    @Enumerated(EnumType.STRING)
    @Column
    private OrigemPedido origem;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.ORCAMENTO;

    // Campos financeiros — obrigatórios para cálculo de margem (RN02)
    // Peso só é obrigatório para FABRICACAO — validado no Service
    @Column(precision = 10, scale = 3)
    private BigDecimal pesoGramas;

    @Column(precision = 10, scale = 2)
    private BigDecimal custoPorGrama;

    @Column(precision = 10, scale = 2)
    private BigDecimal outrosCustos = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoCobrado;

    // Calculados automaticamente pelo Service — nunca editáveis manualmente (RN02)
    @Column(precision = 10, scale = 2)
    private BigDecimal margemBruta;

    @Column(precision = 10, scale = 4)
    private BigDecimal percentualMargem;

    // Sinal pago pelo cliente ao aprovar o orçamento
    @Column(precision = 10, scale = 2)
    private BigDecimal sinal = BigDecimal.ZERO;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // Relacionamentos
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> itens = new ArrayList<>();

    public Pedido() {}

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusPedido.ORCAMENTO;
        }
        if (this.outrosCustos == null) {
            this.outrosCustos = BigDecimal.ZERO;
        }
        if (this.sinal == null) {
            this.sinal = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoPedido getTipoPedido() {
        return tipoPedido;
    }

    public void setTipoPedido(TipoPedido tipoPedido) {
        this.tipoPedido = tipoPedido;
    }

    public TipoPeca getTipoPeca() {
        return tipoPeca;
    }

    public void setTipoPeca(TipoPeca tipoPeca) {
        this.tipoPeca = tipoPeca;
    }

    public OrigemPedido getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemPedido origem) {
        this.origem = origem;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public BigDecimal getPesoGramas() {
        return pesoGramas;
    }

    public void setPesoGramas(BigDecimal pesoGramas) {
        this.pesoGramas = pesoGramas;
    }

    public BigDecimal getCustoPorGrama() {
        return custoPorGrama;
    }

    public void setCustoPorGrama(BigDecimal custoPorGrama) {
        this.custoPorGrama = custoPorGrama;
    }

    public BigDecimal getOutrosCustos() {
        return outrosCustos;
    }

    public void setOutrosCustos(BigDecimal outrosCustos) {
        this.outrosCustos = outrosCustos;
    }

    public BigDecimal getPrecoCobrado() {
        return precoCobrado;
    }

    public void setPrecoCobrado(BigDecimal precoCobrado) {
        this.precoCobrado = precoCobrado;
    }

    public BigDecimal getMargemBruta() {
        return margemBruta;
    }

    public void setMargemBruta(BigDecimal margemBruta) {
        this.margemBruta = margemBruta;
    }

    public BigDecimal getPercentualMargem() {
        return percentualMargem;
    }

    public void setPercentualMargem(BigDecimal percentualMargem) {
        this.percentualMargem = percentualMargem;
    }

    public BigDecimal getSinal() {
        return sinal;
    }

    public void setSinal(BigDecimal sinal) {
        this.sinal = sinal;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<PedidoItem> getItens() {
        return itens;
    }

    public void setItens(List<PedidoItem> itens) {
        this.itens = itens;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}