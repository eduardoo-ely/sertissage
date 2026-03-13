package br.com.sertissage.infrastructure.exception;

import java.math.BigDecimal;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String nomeMaterial, BigDecimal saldoAtual, BigDecimal necessario) {
        super(String.format(
            "Estoque insuficiente para '%s'. Saldo atual: %.3fg | Necessário: %.3fg",
            nomeMaterial, saldoAtual, necessario
        ));
    }
}