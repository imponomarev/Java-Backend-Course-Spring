package edu.java.bot.exceptions;

public class BadRequestException extends RuntimeException {
    private final String description;

    public BadRequestException(String message, String description) {
        super(message);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
