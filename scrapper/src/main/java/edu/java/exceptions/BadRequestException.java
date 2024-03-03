package edu.java.exceptions;

public class BadRequestException extends Exception {
    private final String description;

    public BadRequestException(String message, String description) {
        super(message);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
