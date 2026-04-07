package com.example.api_docker.domain.course;

import com.example.api_docker.domain.shared.exception.DomainException;

import java.math.BigDecimal;

public record Price(BigDecimal amount, CurrencyType currency) {

    /**
     * O construtor sem parâmetros sempre vai rodar, mesmo chamando o construtor com parâmetros
     * Esse é o comportamento padrão dos records
     */
    public Price {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new DomainException("Preço não pode ser negativo");

        if (currency == null)
            throw new DomainException("Moeda não pode ser vazia");
    }

    public static Price of(BigDecimal amount, CurrencyType currency) {
        return new Price(amount, currency);
    }

    public static Price free(CurrencyType currency) {
        return new Price(BigDecimal.ZERO, currency);
    }

    public boolean isFree() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
}
