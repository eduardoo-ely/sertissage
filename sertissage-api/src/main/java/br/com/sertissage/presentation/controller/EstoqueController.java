package br.com.sertissage.presentation.controller;

import br.com.sertissage.application.dto.request.AjusteEstoqueRequest;
import br.com.sertissage.application.dto.request.EntradaEstoqueRequest;
import br.com.sertissage.application.dto.response.MovimentacaoResponse;
import br.com.sertissage.application.dto.response.SaldoResponse;
import br.com.sertissage.domain.enums.OrigemMovimentacao;
import br.com.sertissage.domain.enums.TipoMovimentacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Estoque", description = "Gestão de estoque — movimentações imutáveis (RN04)")
@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final List<MovimentacaoResponse> movimentacoes = new ArrayList<>();

    private final UUID MOCK_MATERIAL_OURO_ID   = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID MOCK_MATERIAL_PRATA_ID  = UUID.fromString("33333333-3333-3333-3333-333333333333");

    public EstoqueController() {
        MovimentacaoResponse entradaOuro = new MovimentacaoResponse();
        entradaOuro.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        entradaOuro.setMaterialId(MOCK_MATERIAL_OURO_ID);
        entradaOuro.setMaterialNome("Ouro 18k");
        entradaOuro.setTipo(TipoMovimentacao.ENTRADA);
        entradaOuro.setOrigem(OrigemMovimentacao.COMPRA);
        entradaOuro.setQuantidadeGramas(new BigDecimal("50.000"));
        entradaOuro.setValorPorGrama(new BigDecimal("350.00"));
        entradaOuro.setValorTotal(new BigDecimal("17500.00"));
        entradaOuro.setObservacao("Compra inicial de ouro 18k");
        entradaOuro.setCreatedAt(LocalDateTime.now());

        MovimentacaoResponse entradaPrata = new MovimentacaoResponse();
        entradaPrata.setId(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        entradaPrata.setMaterialId(MOCK_MATERIAL_PRATA_ID);
        entradaPrata.setMaterialNome("Prata 925");
        entradaPrata.setTipo(TipoMovimentacao.ENTRADA);
        entradaPrata.setOrigem(OrigemMovimentacao.COMPRA);
        entradaPrata.setQuantidadeGramas(new BigDecimal("200.000"));
        entradaPrata.setValorPorGrama(new BigDecimal("45.00"));
        entradaPrata.setValorTotal(new BigDecimal("9000.00"));
        entradaPrata.setObservacao("Compra inicial de prata 925");
        entradaPrata.setCreatedAt(LocalDateTime.now());

        movimentacoes.add(entradaOuro);
        movimentacoes.add(entradaPrata);
    }

    @Operation(summary = "Consultar saldo de um material (GET)")
    @GetMapping("/saldo/{materialId}")
    public ResponseEntity<SaldoResponse> saldo(@PathVariable UUID materialId) {
        BigDecimal saldo = movimentacoes.stream()
                .filter(m -> m.getMaterialId().equals(materialId))
                .map(m -> {
                    if (m.getTipo() == TipoMovimentacao.SAIDA) {
                        return m.getQuantidadeGramas().negate();
                    }
                    return m.getQuantidadeGramas();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String nome = movimentacoes.stream()
                .filter(m -> m.getMaterialId().equals(materialId))
                .map(MovimentacaoResponse::getMaterialNome)
                .findFirst().orElse("-");

        return ResponseEntity.ok(new SaldoResponse(materialId, nome, saldo, "g"));
    }

    @Operation(summary = "Histórico de um material (GET)")
    @GetMapping("/historico/{materialId}")
    public ResponseEntity<List<MovimentacaoResponse>> historico(@PathVariable UUID materialId) {
        List<MovimentacaoResponse> resultado = movimentacoes.stream()
                .filter(m -> m.getMaterialId().equals(materialId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Histórico completo da empresa (GET)")
    @GetMapping("/historico")
    public ResponseEntity<List<MovimentacaoResponse>> historicoCompleto() {
        return ResponseEntity.ok(movimentacoes);
    }

    @Operation(summary = "Registrar entrada de material (POST)")
    @PostMapping("/entrada")
    public ResponseEntity<MovimentacaoResponse> entrada(@Valid @RequestBody EntradaEstoqueRequest request) {
        MovimentacaoResponse mov = new MovimentacaoResponse();
        mov.setId(UUID.randomUUID());
        mov.setMaterialId(request.getMaterialId());
        mov.setMaterialNome("Material ID: " + request.getMaterialId());
        mov.setTipo(TipoMovimentacao.ENTRADA);
        mov.setOrigem(OrigemMovimentacao.COMPRA);
        mov.setQuantidadeGramas(request.getQuantidade());
        mov.setValorPorGrama(request.getValorPorGrama());
        mov.setObservacao(request.getObservacao());
        mov.setCreatedAt(LocalDateTime.now());

        if (request.getValorPorGrama() != null) {
            mov.setValorTotal(request.getQuantidade().multiply(request.getValorPorGrama()));
        }

        movimentacoes.add(mov);
        return ResponseEntity.status(HttpStatus.CREATED).body(mov);
    }

    @Operation(summary = "Registrar ajuste de estoque (POST)")
    @PostMapping("/ajuste")
    public ResponseEntity<MovimentacaoResponse> ajuste(@Valid @RequestBody AjusteEstoqueRequest request) {
        boolean ehSaida = request.getQuantidade().compareTo(BigDecimal.ZERO) < 0;

        MovimentacaoResponse mov = new MovimentacaoResponse();
        mov.setId(UUID.randomUUID());
        mov.setMaterialId(request.getMaterialId());
        mov.setMaterialNome("Material ID: " + request.getMaterialId());
        mov.setTipo(ehSaida ? TipoMovimentacao.SAIDA : TipoMovimentacao.AJUSTE);
        mov.setOrigem(OrigemMovimentacao.AJUSTE_MANUAL);
        mov.setQuantidadeGramas(request.getQuantidade().abs());
        mov.setObservacao(request.getObservacao());
        mov.setCreatedAt(LocalDateTime.now());

        movimentacoes.add(mov);
        return ResponseEntity.status(HttpStatus.CREATED).body(mov);
    }
}