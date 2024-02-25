package com.ajaz.userservice3.exceptions;


public class WrongCredentialsException extends Exception{
    public WrongCredentialsException(String message){
        super(message);
    }
}
