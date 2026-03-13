package br.com.sertissage.infrastructure.exception;

import br.com.sertissage.domain.enums.StatusPedido;

public class TransicaoStatusInvalidaException extends RuntimeException {

    public TransicaoStatusInvalidaException(StatusPedido atual, StatusPedido destino) {
        super(String.format(
            "Transição de status inválida: '%s' → '%s'",
            atual, destino
        ));
    }
}