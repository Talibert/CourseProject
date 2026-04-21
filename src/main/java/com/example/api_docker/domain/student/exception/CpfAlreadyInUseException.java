package com.example.api_docker.domain.student.exception;

import com.example.api_docker.domain.student.Cpf;

public class CpfAlreadyInUseException extends RuntimeException{

    public CpfAlreadyInUseException(String cpf){

    }
}
