package com.example.springbootsoaphospital.Exceptions;

public class ResourceRegisterFirstException extends RuntimeException {

    public ResourceRegisterFirstException(String resourceName, String fieldName, String value, String regName) {
        super(String.format("Registration required for %s with %s %s as %s", resourceName, fieldName, value, regName));
    }
}