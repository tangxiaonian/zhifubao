package com.example.demo_springboot.exception;

public class QueryOrderException extends RuntimeException{
    public QueryOrderException(String message) {
        super(message);
    }
}
