package br.com.sertissage.presentation.controller;

import br.com.sertissage.application.dto.request.AjusteEstoqueRequest;
import br.com.sertissage.application.dto.request.EntradaEstoqueRequest;
import br.com.sertissage.application.dto.response.MovimentacaoResponse;
import br.com.sertissage.application.dto.response.SaldoResponse;
import br.com.sertissage.application.service.EstoqueService;
import br.com.sertissage.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Estoque")
@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @Operation(summary = "Consultar saldo de um material")
    @GetMapping("/saldo/{materialId}")
    public ResponseEntity<SaldoResponse> saldo(@PathVariable UUID materialId) {
        UUID empresaId = SecurityUtils.getEmpresaId();

        var saldo = estoqueService.calcularSaldo(empresaId, materialId);

        var historico = estoqueService.consultarHistorico(empresaId, materialId);
        String nome = historico.stream()
                .findFirst()
                .map(m -> m.getMaterial().getNome())
                .orElse("-");
        String unidade = historico.stream()
                .findFirst()
                .map(m -> m.getMaterial().getUnidadeMedida())
                .orElse("-");

        return ResponseEntity.ok(new SaldoResponse(materialId, nome, saldo, unidade));
    }

    @Operation(summary = "Histórico de movimentações de um material")
    @GetMapping("/historico/{materialId}")
    public ResponseEntity<List<MovimentacaoResponse>> historico(@PathVariable UUID materialId) {
        UUID empresaId = SecurityUtils.getEmpresaId();
        return ResponseEntity.ok(
                estoqueService.consultarHistorico(empresaId, materialId)
                        .stream().map(MovimentacaoResponse::de).collect(Collectors.toList())
        );
    }

    @Operation(summary = "Histórico completo da empresa")
    @GetMapping("/historico")
    public ResponseEntity<List<MovimentacaoResponse>> historicoCompleto() {
        return ResponseEntity.ok(
                estoqueService.consultarHistoricoCompleto(SecurityUtils.getEmpresaId())
                        .stream().map(MovimentacaoResponse::de).collect(Collectors.toList())
        );
    }

    @Operation(summary = "Registrar entrada de material")
    @PostMapping("/entrada")
    public ResponseEntity<MovimentacaoResponse> entrada(@Valid @RequestBody EntradaEstoqueRequest request) {
        var mov = estoqueService.registrarEntrada(
                SecurityUtils.getEmpresaId(),
                request.getMaterialId(),
                SecurityUtils.getUsuarioId(),
                request.getQuantidade(),
                request.getValorPorGrama(),
                request.getObservacao()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoResponse.de(mov));
    }

    @Operation(summary = "Registrar ajuste de estoque")
    @PostMapping("/ajuste")
    public ResponseEntity<MovimentacaoResponse> ajuste(@Valid @RequestBody AjusteEstoqueRequest request) {
        var mov = estoqueService.registrarAjuste(
                SecurityUtils.getEmpresaId(),
                request.getMaterialId(),
                SecurityUtils.getUsuarioId(),
                request.getQuantidade(),
                request.getObservacao()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimentacaoResponse.de(mov));
    }
}