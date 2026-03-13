package br.com.sertissage.infrastructure.security;

import java.util.UUID;

public class SecurityUtils {

    public static UUID getEmpresaId() {
        return UUID.randomUUID();
    }

    public static UUID getUsuarioId() {
        return UUID.randomUUID();
    }

}