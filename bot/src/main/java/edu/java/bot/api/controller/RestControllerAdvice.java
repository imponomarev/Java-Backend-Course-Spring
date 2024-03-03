package edu.java.bot.api.controller;

import edu.java.bot.api.model.ApiErrorResponse;
import edu.java.bot.exceptions.UpdateAlreadyExistsException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(UpdateAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUpdateAlreadyExistsException(UpdateAlreadyExistsException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ApiErrorResponse(
                "The update already exists",
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .toList()
            ));
    }
}
