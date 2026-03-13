package br.com.sertissage.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SaldoResponse {

    private UUID materialId;
    private String materialNome;
    private BigDecimal saldo;
    private String unidadeMedida;
}