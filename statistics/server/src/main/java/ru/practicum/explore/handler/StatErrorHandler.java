package ru.practicum.explore.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explore.exception.DataError;

@RestControllerAdvice
public class StatErrorHandler {

    @ExceptionHandler({DataError.class})
    public ResponseEntity<String> handleBadRequestException(final DataError e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
