package com.pm.patientservice.exception;

public class EmailAlreadyExist extends RuntimeException{
    public EmailAlreadyExist(String message)
    {
        super(message);
    }
}
