package br.com.sertissage.application.service;

import br.com.sertissage.domain.entity.CategoriaMaterial;
import br.com.sertissage.domain.entity.Material;
import br.com.sertissage.domain.enums.TipoCategoria;
import br.com.sertissage.domain.repository.CategoriaMaterialRepository;
import br.com.sertissage.domain.repository.MaterialRepository;
import br.com.sertissage.domain.repository.PedidoItemRepository;
import br.com.sertissage.infrastructure.exception.RegraNegocioException;
import br.com.sertissage.infrastructure.exception.ResourceNotFoundException;
import br.com.sertissage.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepo;
    private final CategoriaMaterialRepository categoriaRepo;
    private final PedidoItemRepository pedidoItemRepo;

    @Transactional(readOnly = true)
    public List<Material> listar() {
        return materialRepo.findDisponiveis(SecurityUtils.getEmpresaId());
    }

    @Transactional(readOnly = true)
    public List<Material> listarPorCategoria(TipoCategoria tipoCategoria) {
        return materialRepo.findDisponiveisPorCategoria(SecurityUtils.getEmpresaId(), tipoCategoria);
    }

    @Transactional(readOnly = true)
    public List<Material> listarPropriosDaEmpresa() {
        return materialRepo.findByEmpresaId(SecurityUtils.getEmpresaId());
    }

    @Transactional(readOnly = true)
    public Material buscarPorId(UUID id) {
        return buscarOuErro(id);
    }

    @Transactional
    public Material criar(Material material, UUID categoriaId) {
        UUID empresaId = SecurityUtils.getEmpresaId();

        if (materialRepo.existsByNomeAndEmpresaId(material.getNome(), empresaId)) {
            throw new RegraNegocioException("Já existe um material com o nome '" + material.getNome() + "'.");
        }

        CategoriaMaterial categoria = categoriaRepo.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", categoriaId));

        material.setEmpresa(SecurityUtils.getUsuarioAutenticado().getEmpresa());
        material.setCategoria(categoria);
        material.setAtivo(true);

        Material salvo = materialRepo.save(material);
        log.info("[Material] Criado — id={} | empresa={}", salvo.getId(), empresaId);
        return salvo;
    }

    @Transactional
    public Material atualizar(UUID id, Material dados, UUID categoriaId) {
        UUID empresaId = SecurityUtils.getEmpresaId();
        Material material = buscarOuErro(id);

        if (material.isGlobal()) {
            throw new RegraNegocioException("Materiais do catálogo global não podem ser editados.");
        }

        if (!dados.getNome().equals(material.getNome())
                && materialRepo.existsByNomeAndEmpresaId(dados.getNome(), empresaId)) {
            throw new RegraNegocioException("Já existe um material com o nome '" + dados.getNome() + "'.");
        }

        if (categoriaId != null) {
            CategoriaMaterial categoria = categoriaRepo.findById(categoriaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria", categoriaId));
            material.setCategoria(categoria);
        }

        material.setNome(dados.getNome());
        material.setObservacao(dados.getObservacao());
        material.setUnidadeMedida(dados.getUnidadeMedida());

        return materialRepo.save(material);
    }

    @Transactional
    public Material desativar(UUID id) {
        Material material = buscarOuErro(id);

        if (material.isGlobal()) {
            throw new RegraNegocioException("Materiais do catálogo global não podem ser desativados.");
        }

        if (pedidoItemRepo.existsByMaterialId(id)) {
            throw new RegraNegocioException("Não é possível desativar um material vinculado a pedidos.");
        }

        material.setAtivo(false);
        log.info("[Material] Desativado — id={}", id);
        return materialRepo.save(material);
    }

    @Transactional
    public Material ativar(UUID id) {
        Material material = buscarOuErro(id);

        if (material.isGlobal()) {
            throw new RegraNegocioException("Materiais do catálogo global não podem ser alterados.");
        }

        material.setAtivo(true);
        log.info("[Material] Ativado — id={}", id);
        return materialRepo.save(material);
    }

    private Material buscarOuErro(UUID id) {
        return materialRepo.findByIdAndEmpresaIdOrGlobal(id, SecurityUtils.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Material", id));
    }
}