package edu.java.bot.exceptions;

import edu.java.bot.api.model.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ApiErrorException extends RuntimeException {
    private final ApiErrorResponse apiErrorResponse;

    public ApiErrorException(ApiErrorResponse apiErrorResponse) {
        this.apiErrorResponse = apiErrorResponse;
    }
}
