package br.com.sertissage.application.service;

import br.com.sertissage.domain.entity.Cliente;
import br.com.sertissage.domain.repository.ClienteRepository;
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
public class ClienteService {

    private final ClienteRepository clienteRepo;

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepo.findByEmpresaId(SecurityUtils.getEmpresaId());
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepo.findByEmpresaIdAndNomeContainingIgnoreCase(SecurityUtils.getEmpresaId(), nome);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(UUID id) {
        return buscarOuErro(id);
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        UUID empresaId = SecurityUtils.getEmpresaId();

        if (cliente.getTelefone() != null && !cliente.getTelefone().isBlank()) {
            if (clienteRepo.existsByEmpresaIdAndTelefone(empresaId, cliente.getTelefone())) {
                throw new RegraNegocioException("Já existe um cliente com o telefone '" + cliente.getTelefone() + "'.");
            }
        }

        if (cliente.getEmail() != null && !cliente.getEmail().isBlank()) {
            if (clienteRepo.existsByEmpresaIdAndEmail(empresaId, cliente.getEmail())) {
                throw new RegraNegocioException("Já existe um cliente com o email '" + cliente.getEmail() + "'.");
            }
        }

        cliente.setEmpresa(SecurityUtils.getUsuarioAutenticado().getEmpresa());

        Cliente salvo = clienteRepo.save(cliente);
        log.info("[Cliente] Criado — id={} | empresa={}", salvo.getId(), empresaId);
        return salvo;
    }

    @Transactional
    public Cliente atualizar(UUID id, Cliente dados) {
        UUID empresaId = SecurityUtils.getEmpresaId();
        Cliente cliente = buscarOuErro(id);

        if (dados.getTelefone() != null && !dados.getTelefone().isBlank()
                && !dados.getTelefone().equals(cliente.getTelefone())) {
            if (clienteRepo.existsByEmpresaIdAndTelefone(empresaId, dados.getTelefone())) {
                throw new RegraNegocioException("Já existe um cliente com o telefone '" + dados.getTelefone() + "'.");
            }
        }

        if (dados.getEmail() != null && !dados.getEmail().isBlank()
                && !dados.getEmail().equals(cliente.getEmail())) {
            if (clienteRepo.existsByEmpresaIdAndEmail(empresaId, dados.getEmail())) {
                throw new RegraNegocioException("Já existe um cliente com o email '" + dados.getEmail() + "'.");
            }
        }

        cliente.setNome(dados.getNome());
        cliente.setTelefone(dados.getTelefone());
        cliente.setEmail(dados.getEmail());
        cliente.setObservacao(dados.getObservacao());

        return clienteRepo.save(cliente);
    }

    @Transactional
    public void deletar(UUID id) {
        Cliente cliente = buscarOuErro(id);
        clienteRepo.delete(cliente);
        log.info("[Cliente] Removido — id={} | empresa={}", id, SecurityUtils.getEmpresaId());
    }

    private Cliente buscarOuErro(UUID id) {
        return clienteRepo.findByIdAndEmpresaId(id, SecurityUtils.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }
}