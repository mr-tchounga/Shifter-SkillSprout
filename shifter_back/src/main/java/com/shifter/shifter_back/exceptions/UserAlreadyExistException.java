package com.shifter.shifter_back.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserAlreadyExistException extends RuntimeException{

    public UserAlreadyExistException(String email) {
        super(String.format("User with email [%s] already exist", email));
    }
}
