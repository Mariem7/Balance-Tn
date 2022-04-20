package com.backendspringboot.exception;

public class UsernameNotFoundException extends Exception{
    public UsernameNotFoundException(String message) {
        super(message);
    }
}
