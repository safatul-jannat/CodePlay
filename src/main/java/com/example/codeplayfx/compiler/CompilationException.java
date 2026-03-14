package com.example.codeplayfx.compiler;

/*
CompilationException - Thrown when user code compilation fails.
 */
public class CompilationException extends RuntimeException {
    
    public CompilationException(String message) {
        super(message);
    }
    
    public CompilationException(String message, Throwable cause) {
        super(message, cause);
    }
}