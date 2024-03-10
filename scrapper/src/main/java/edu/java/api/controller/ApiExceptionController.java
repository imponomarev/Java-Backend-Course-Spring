package edu.java.api.controller;

import edu.java.api.model.ApiErrorResponse;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ApiErrorResponse handleBadRequestException(BadRequestException e) {
            return new ApiErrorResponse(
                e.getDescription(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .toList()
            );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotFoundException.class)
    public ApiErrorResponse handleNotFoundException(NotFoundException e) {
        return new ApiErrorResponse(
                e.getDescription(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .toList()
            );
    }
}
