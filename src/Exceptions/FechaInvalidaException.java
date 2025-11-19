package Exceptions;

public class FechaInvalidaException extends RuntimeException {
    public FechaInvalidaException(String message) {
        super(message);
    }
}