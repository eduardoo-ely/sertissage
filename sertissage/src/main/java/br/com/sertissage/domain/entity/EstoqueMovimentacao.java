package br.com.sertissage.domain.entity;

import br.com.sertissage.domain.enums.OrigemMovimentacao;
import br.com.sertissage.domain.enums.TipoMovimentacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "estoque_movimentacao", indexes = {
    @Index(name = "idx_estoque_material", columnList = "material_id"),
    @Index(name = "idx_estoque_empresa", columnList = "empresa_id"),
    @Index(name = "idx_estoque_pedido", columnList = "pedido_id"),
    @Index(name = "idx_estoque_created", columnList = "created_at")
})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Enumerated(EnumType.STRING)
    @Column
    private OrigemMovimentacao origem;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidadeGramas;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorPorGrama;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        
        if (this.valorPorGrama != null && this.valorTotal == null) {
            this.valorTotal = this.quantidadeGramas.multiply(this.valorPorGrama);
        }
    }

}