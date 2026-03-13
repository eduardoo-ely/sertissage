package br.com.sertissage.domain.repository;

import br.com.sertissage.domain.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    List<Cliente> findByEmpresaId(UUID empresaId);

    Optional<Cliente> findByIdAndEmpresaId(UUID id, UUID empresaId);

    List<Cliente> findByEmpresaIdAndNomeContainingIgnoreCase(UUID empresaId, String nome);

    boolean existsByEmpresaIdAndTelefone(UUID empresaId, String telefone);

    boolean existsByEmpresaIdAndEmail(UUID empresaId, String email);
}