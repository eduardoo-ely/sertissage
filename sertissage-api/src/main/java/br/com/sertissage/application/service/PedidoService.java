package br.com.sertissage.application.service;

import br.com.sertissage.domain.entity.*;
import br.com.sertissage.domain.enums.StatusPedido;
import br.com.sertissage.domain.repository.*;
import br.com.sertissage.infrastructure.exception.RegraNegocioException;
import br.com.sertissage.infrastructure.exception.ResourceNotFoundException;
import br.com.sertissage.infrastructure.exception.TransicaoStatusInvalidaException;
import br.com.sertissage.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepo;
    private final ClienteRepository clienteRepo;
    private final EstoqueService estoqueService;

    @Transactional(readOnly = true)
    public List<Pedido> listar() {
        return pedidoRepo.findByEmpresaIdOrderByCreatedAtDesc(SecurityUtils.getEmpresaId());
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorStatus(StatusPedido status) {
        return pedidoRepo.findByEmpresaIdAndStatusOrderByCreatedAtDesc(SecurityUtils.getEmpresaId(), status);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(UUID clienteId) {
        return pedidoRepo.findByEmpresaIdAndClienteIdOrderByCreatedAtDesc(SecurityUtils.getEmpresaId(), clienteId);
    }

    @Transactional(readOnly = true)
    public Pedido buscarPorId(UUID id) {
        return buscarOuErro(id);
    }

    @Transactional
    public Pedido criar(Pedido pedido) {
        UUID empresaId = SecurityUtils.getEmpresaId();

        Cliente cliente = clienteRepo.findByIdAndEmpresaId(pedido.getCliente().getId(), empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", pedido.getCliente().getId()));

        pedido.setCliente(cliente);
        pedido.setEmpresa(cliente.getEmpresa());
        pedido.setStatus(StatusPedido.ORCAMENTO);

        calcularMargem(pedido);

        Pedido salvo = pedidoRepo.save(pedido);
        log.info("[Pedido] Criado — id={} | empresa={}", salvo.getId(), empresaId);
        return salvo;
    }

    @Transactional
    public Pedido atualizar(UUID id, Pedido dados) {
        Pedido pedido = buscarOuErro(id);

        if (pedido.getStatus() != StatusPedido.ORCAMENTO &&
            pedido.getStatus() != StatusPedido.AGUARDANDO_SINAL) {
            throw new RegraNegocioException(
                "Pedido só pode ser editado nos status ORCAMENTO ou AGUARDANDO_SINAL. Status atual: " + pedido.getStatus());
        }

        pedido.setDescricao(dados.getDescricao());
        pedido.setTipoPedido(dados.getTipoPedido());
        pedido.setTipoPeca(dados.getTipoPeca());
        pedido.setOrigem(dados.getOrigem());
        pedido.setPesoGramas(dados.getPesoGramas());
        pedido.setCustoPorGrama(dados.getCustoPorGrama());
        pedido.setOutrosCustos(dados.getOutrosCustos());
        pedido.setPrecoCobrado(dados.getPrecoCobrado());
        pedido.setSinal(dados.getSinal());

        calcularMargem(pedido);

        return pedidoRepo.save(pedido);
    }

    @Transactional
    public Pedido avancarStatus(UUID id) {
        Pedido pedido = buscarOuErro(id);
        StatusPedido atual = pedido.getStatus();
        StatusPedido proximo = proximoStatus(atual);

        validarTransicao(atual, proximo);
        executarRegrasDeTransicao(pedido, atual, proximo);

        pedido.setStatus(proximo);
        Pedido salvo = pedidoRepo.save(pedido);
        log.info("[Pedido] Transição — id={} | {} → {}", id, atual, proximo);
        return salvo;
    }

    @Transactional
    public Pedido cancelar(UUID id, String motivo) {
        Pedido pedido = buscarOuErro(id);

        if (pedido.getStatus() == StatusPedido.FINALIZADO) {
            throw new RegraNegocioException("Pedido finalizado não pode ser cancelado.");
        }

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraNegocioException("Pedido já está cancelado.");
        }

        if (pedido.getStatus() == StatusPedido.EM_PRODUCAO) {
            log.warn("[Pedido] Cancelado em EM_PRODUCAO — material NÃO retorna ao estoque. id={} | motivo={}", id, motivo);
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setDescricao(pedido.getDescricao() != null
                ? pedido.getDescricao() + " | CANCELADO: " + motivo
                : "CANCELADO: " + motivo);

        Pedido salvo = pedidoRepo.save(pedido);
        log.info("[Pedido] Cancelado — id={}", id);
        return salvo;
    }

    private void calcularMargem(Pedido pedido) {
        BigDecimal peso = pedido.getPesoGramas();
        BigDecimal custoPorGrama = pedido.getCustoPorGrama();
        BigDecimal preco = pedido.getPrecoCobrado();
        BigDecimal outros = pedido.getOutrosCustos() != null ? pedido.getOutrosCustos() : BigDecimal.ZERO;

        if (peso == null || custoPorGrama == null || preco == null || preco.compareTo(BigDecimal.ZERO) == 0) {
            pedido.setMargemBruta(null);
            pedido.setPercentualMargem(null);
            return;
        }

        BigDecimal custoTotal = peso.multiply(custoPorGrama).add(outros);
        BigDecimal margemBruta = preco.subtract(custoTotal);
        BigDecimal percentual = margemBruta
                .divide(preco, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(4, RoundingMode.HALF_UP);

        pedido.setMargemBruta(margemBruta.setScale(2, RoundingMode.HALF_UP));
        pedido.setPercentualMargem(percentual);
    }

    private Pedido buscarOuErro(UUID id) {
        return pedidoRepo.findByIdAndEmpresaId(id, SecurityUtils.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
    }

    private StatusPedido proximoStatus(StatusPedido atual) {
        return switch (atual) {
            case ORCAMENTO        -> StatusPedido.AGUARDANDO_SINAL;
            case AGUARDANDO_SINAL -> StatusPedido.APROVADO;
            case APROVADO         -> StatusPedido.EM_PRODUCAO;
            case EM_PRODUCAO      -> StatusPedido.FINALIZADO;
            case FINALIZADO       -> throw new TransicaoStatusInvalidaException(atual, StatusPedido.FINALIZADO);
            case CANCELADO        -> throw new TransicaoStatusInvalidaException(atual, StatusPedido.CANCELADO);
        };
    }

    private void validarTransicao(StatusPedido atual, StatusPedido destino) {
        if (atual == StatusPedido.FINALIZADO || atual == StatusPedido.CANCELADO) {
            throw new TransicaoStatusInvalidaException(atual, destino);
        }
    }

    private void executarRegrasDeTransicao(Pedido pedido, StatusPedido atual, StatusPedido proximo) {
        if (atual == StatusPedido.APROVADO && proximo == StatusPedido.EM_PRODUCAO) {
            Usuario usuario = SecurityUtils.getUsuarioAutenticado();
            estoqueService.gerarSaidasDoPedido(pedido, usuario);
        }
    }
}