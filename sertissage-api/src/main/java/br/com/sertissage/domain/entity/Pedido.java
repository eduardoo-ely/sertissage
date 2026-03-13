package br.com.sertissage.domain.entity;

import br.com.sertissage.domain.enums.OrigemPedido;
import br.com.sertissage.domain.enums.StatusPedido;
import br.com.sertissage.domain.enums.TipoPedido;
import br.com.sertissage.domain.enums.TipoPeca;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pedido", indexes = {
    @Index(name = "idx_pedido_empresa", columnList = "empresa_id"),
    @Index(name = "idx_pedido_cliente", columnList = "cliente_id"),
    @Index(name = "idx_pedido_status", columnList = "status"),
    @Index(name = "idx_pedido_created", columnList = "created_at")
})
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPedido tipoPedido;

    @Enumerated(EnumType.STRING)
    @Column
    private TipoPeca tipoPeca;

    @Enumerated(EnumType.STRING)
    @Column
    private OrigemPedido origem;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusPedido status = StatusPedido.ORCAMENTO;

    // Campos financeiros
    @Column(precision = 10, scale = 3)
    private BigDecimal pesoGramas;

    @Column(precision = 10, scale = 2)
    private BigDecimal custoPorGrama;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal outrosCustos = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoCobrado;

    // Calculados automaticamente (RN02)
    @Column(precision = 10, scale = 2)
    private BigDecimal margemBruta;

    @Column(precision = 10, scale = 4)
    private BigDecimal percentualMargem;

    @Column(precision = 10, scale = 2)
    @Builder.Default
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
    @Builder.Default
    private List<PedidoItem> itens = new ArrayList<>();

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

    // ========== Helper Methods para manter sincronização bidirecional ==========

    public void adicionarItem(PedidoItem item) {
        itens.add(item);
        item.setPedido(this);
    }

    public void removerItem(PedidoItem item) {
        itens.remove(item);
        item.setPedido(null);
    }

    public void limparItens() {
        itens.forEach(item -> item.setPedido(null));
        itens.clear();
    }
}