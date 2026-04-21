package com.example.api_docker.domain.student.exception;

import com.example.api_docker.domain.student.Email;

public class EmailAlreadyInUseException extends RuntimeException{

    public EmailAlreadyInUseException(String email){

    }
}
