package br.com.sertissage.presentation.controller;

import br.com.sertissage.application.dto.request.ClienteRequest;
import br.com.sertissage.application.dto.response.ClienteResponse;
import br.com.sertissage.application.service.ClienteService;
import br.com.sertissage.domain.entity.Cliente;
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

@Tag(name = "Clientes")
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @Operation(summary = "Listar clientes")
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar() {
        return ResponseEntity.ok(clienteService.listar()
                .stream().map(ClienteResponse::de).collect(Collectors.toList()));
    }

    @Operation(summary = "Buscar cliente por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(ClienteResponse.de(clienteService.buscarPorId(id)));
    }

    @Operation(summary = "Buscar clientes por nome")
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponse>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(clienteService.buscarPorNome(nome)
                .stream().map(ClienteResponse::de).collect(Collectors.toList()));
    }

    @Operation(summary = "Criar cliente")
    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setTelefone(request.getTelefone());
        cliente.setEmail(request.getEmail());
        cliente.setObservacao(request.getObservacao());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ClienteResponse.de(clienteService.criar(cliente)));
    }

    @Operation(summary = "Atualizar cliente")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ClienteRequest request) {

        Cliente dados = new Cliente();
        dados.setNome(request.getNome());
        dados.setTelefone(request.getTelefone());
        dados.setEmail(request.getEmail());
        dados.setObservacao(request.getObservacao());

        return ResponseEntity.ok(ClienteResponse.de(clienteService.atualizar(id, dados)));
    }

    @Operation(summary = "Remover cliente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}