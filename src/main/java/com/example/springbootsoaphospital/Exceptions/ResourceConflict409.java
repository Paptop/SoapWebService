package com.example.springbootsoaphospital.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceConflict409 extends RuntimeException {
    public ResourceConflict409(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("Entity %s  with  %s : '%s' already present", resourceName, fieldName, fieldValue));
    }
}
