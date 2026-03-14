package br.com.sertissage.presentation.controller;

import br.com.sertissage.application.dto.request.MaterialRequest;
import br.com.sertissage.application.dto.response.MaterialResponse;
import br.com.sertissage.application.service.MaterialService;
import br.com.sertissage.domain.entity.Material;
import br.com.sertissage.domain.enums.TipoCategoria;
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

@Tag(name = "Materiais")
@RestController
@RequestMapping("/api/materiais")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @Operation(summary = "Listar materiais disponíveis")
    @GetMapping
    public ResponseEntity<List<MaterialResponse>> listar() {
        return ResponseEntity.ok(materialService.listar()
                .stream().map(MaterialResponse::de).collect(Collectors.toList()));
    }

    @Operation(summary = "Listar por categoria")
    @GetMapping("/categoria/{tipo}")
    public ResponseEntity<List<MaterialResponse>> listarPorCategoria(@PathVariable TipoCategoria tipo) {
        return ResponseEntity.ok(materialService.listarPorCategoria(tipo)
                .stream().map(MaterialResponse::de).collect(Collectors.toList()));
    }

    @Operation(summary = "Buscar material por ID")
    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(MaterialResponse.de(materialService.buscarPorId(id)));
    }

    @Operation(summary = "Criar material")
    @PostMapping
    public ResponseEntity<MaterialResponse> criar(@Valid @RequestBody MaterialRequest request) {
        Material material = new Material();
        material.setNome(request.getNome());
        material.setObservacao(request.getObservacao());
        material.setUnidadeMedida(request.getUnidadeMedida());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MaterialResponse.de(materialService.criar(material, request.getCategoriaId())));
    }

    @Operation(summary = "Atualizar material")
    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody MaterialRequest request) {

        Material dados = new Material();
        dados.setNome(request.getNome());
        dados.setObservacao(request.getObservacao());
        dados.setUnidadeMedida(request.getUnidadeMedida());

        return ResponseEntity.ok(MaterialResponse.de(
                materialService.atualizar(id, dados, request.getCategoriaId())));
    }

    @Operation(summary = "Desativar material")
    @DeleteMapping("/{id}")
    public ResponseEntity<MaterialResponse> desativar(@PathVariable UUID id) {
        return ResponseEntity.ok(MaterialResponse.de(materialService.desativar(id)));
    }

    @Operation(summary = "Reativar material")
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<MaterialResponse> ativar(@PathVariable UUID id) {
        return ResponseEntity.ok(MaterialResponse.de(materialService.ativar(id)));
    }
}