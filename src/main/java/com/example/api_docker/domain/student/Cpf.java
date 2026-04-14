package com.example.api_docker.domain.student;

import com.example.api_docker.domain.shared.exception.DomainException;

public record Cpf(String value) {

    public Cpf {
        value = value.replaceAll("[.\\-]", "");
        if (!isValid(value))
            throw new DomainException("CPF inválido: " + value);

    }

    private static boolean isValid(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1)
            return false;

        int sum = 0;

        for (int i = 0; i < 9; i++)
            sum += (cpf.charAt(i) - '0') * (10 - i);

        int first = (sum * 10 % 11) % 10;

        if (first != cpf.charAt(9) - '0')
            return false;

        sum = 0;

        for (int i = 0; i < 10; i++)
            sum += (cpf.charAt(i) - '0') * (11 - i);

        int second = (sum * 10 % 11) % 10;

        return second == cpf.charAt(10) - '0';
    }
}
