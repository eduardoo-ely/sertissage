package br.com.sertissage.domain.repository;

import br.com.sertissage.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    // Listagem filtrada por empresa — sempre usar este método
    List<Cliente> findByEmpresaId(UUID empresaId);

    // Busca por ID garantindo isolamento multiempresa
    Optional<Cliente> findByIdAndEmpresaId(UUID id, UUID empresaId);

    // Busca por nome parcial dentro da empresa
    List<Cliente> findByEmpresaIdAndNomeContainingIgnoreCase(UUID empresaId, String nome);

    // Verifica duplicidade de telefone na mesma empresa
    boolean existsByEmpresaIdAndTelefone(UUID empresaId, String telefone);

    // Verifica duplicidade de email na mesma empresa
    boolean existsByEmpresaIdAndEmail(UUID empresaId, String email);
}