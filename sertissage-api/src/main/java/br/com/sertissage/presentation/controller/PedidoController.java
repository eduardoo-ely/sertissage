package br.com.sertissage.presentation.controller;

import br.com.sertissage.application.dto.request.CancelarPedidoRequest;
import br.com.sertissage.application.dto.request.PedidoRequest;
import br.com.sertissage.application.dto.response.PedidoResponse;
import br.com.sertissage.application.service.PedidoService;
import br.com.sertissage.domain.entity.Cliente;
import br.com.sertissage.domain.entity.Material;
import br.com.sertissage.domain.entity.Pedido;
import br.com.sertissage.domain.entity.PedidoItem;
import br.com.sertissage.domain.enums.StatusPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Pedidos")
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Listar pedidos")
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listar() {
        return ResponseEntity.ok(pedidoService.listar()
                .stream().map(PedidoResponse::de).collect(Collectors.toList()));
    }

    @Operation(summary = "Listar pedidos por status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PedidoResponse>> listarPorStatus(@PathVariable StatusPedido status) {
        return ResponseEntity.ok(pedidoService.listarPorStatus(status)
                .stream().map(PedidoResponse::de).collect(Collectors.toList()));
    }

    @Operation(summary = "Listar pedidos por cliente")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> listarPorCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId)
                .stream().map(PedidoResponse::de).collect(Collectors.toList()));
    }

    @Operation(summary = "Buscar pedido por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(PedidoResponse.de(pedidoService.buscarPorId(id)));
    }

    @Operation(summary = "Criar pedido")
    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PedidoResponse.de(pedidoService.criar(montarPedido(request))));
    }

    @Operation(summary = "Atualizar pedido")
    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(PedidoResponse.de(pedidoService.atualizar(id, montarPedido(request))));
    }

    @Operation(summary = "Avançar status do pedido")
    @PatchMapping("/{id}/avancar")
    public ResponseEntity<PedidoResponse> avancarStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(PedidoResponse.de(pedidoService.avancarStatus(id)));
    }

    @Operation(summary = "Cancelar pedido")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelar(
            @PathVariable UUID id,
            @Valid @RequestBody CancelarPedidoRequest request) {
        return ResponseEntity.ok(PedidoResponse.de(pedidoService.cancelar(id, request.getMotivo())));
    }

    private Pedido montarPedido(PedidoRequest request) {
        Cliente cliente = new Cliente();
        cliente.setId(request.getClienteId());

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .tipoPedido(request.getTipoPedido())
                .tipoPeca(request.getTipoPeca())
                .origem(request.getOrigem())
                .descricao(request.getDescricao())
                .pesoGramas(request.getPesoGramas())
                .custoPorGrama(request.getCustoPorGrama())
                .outrosCustos(request.getOutrosCustos())
                .precoCobrado(request.getPrecoCobrado())
                .sinal(request.getSinal())
                .build();

        if (request.getItens() != null) {
            request.getItens().forEach(itemReq -> {
                Material material = new Material();
                material.setId(itemReq.getMaterialId());
                
                PedidoItem item = PedidoItem.builder()
                        .material(material)
                        .pesoGramas(itemReq.getPesoGramas())
                        .observacao(itemReq.getObservacao())
                        .build();
                        
                pedido.adicionarItem(item); 
            });
        }

        return pedido;
    }
}