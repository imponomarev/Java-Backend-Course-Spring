package edu.java.bot.api.controller;

import edu.java.bot.api.model.ApiErrorResponse;
import edu.java.bot.exceptions.UpdateAlreadyExistsException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UpdateAlreadyExistsException.class)
    public ApiErrorResponse handleUpdateAlreadyExistsException(UpdateAlreadyExistsException e) {
        return
            new ApiErrorResponse(
                "The update already exists",
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .toList()
            );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
            .getAllErrors()
            .stream()
            .map(error -> {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    return fieldError.getField() + " " + error.getDefaultMessage();
                } else {
                    return error.getDefaultMessage();
                }
            })
            .collect(Collectors.toList());
        return new ApiErrorResponse(
            "Validation error",
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            e.getClass().getSimpleName(),
            "Validation failed for some fields",
            errors
        );
    }
}
