package br.com.sertissage.infrastructure.security;

import br.com.sertissage.domain.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario)) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado no contexto de segurança.");
        }
        return (Usuario) auth.getPrincipal();
    }

    public static UUID getEmpresaId() {
        return getUsuarioAutenticado().getEmpresa().getId();
    }

    public static UUID getUsuarioId() {
        return getUsuarioAutenticado().getId();
    }
}