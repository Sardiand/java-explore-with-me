package ru.practicum.explore.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explore.exception.BadRequestException;
import ru.practicum.explore.exception.ConflictException;
import ru.practicum.explore.exception.ForbiddenException;
import ru.practicum.explore.exception.NotFoundException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ApiError> handleNotFoundException(final RuntimeException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(makeResponse(e.getMessage(),
                        "The required object was not found.",
                        HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler({ConflictException.class, IllegalArgumentException.class,
            DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> handleConflictAndViolationException(final RuntimeException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(makeResponse(e.getMessage(),
                        "For the requested operation the conditions are not met.",
                         HttpStatus.CONFLICT));
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ApiError> handleBadRequestException(final RuntimeException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(makeResponse(e.getMessage(),
                        "For the requested operation the conditions are not met.",
                        HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<ApiError> handleForbiddenException(final RuntimeException e) {
        log.debug(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(makeResponse(e.getMessage(),
                        "Insufficient rights to perform this operation.",
                        HttpStatus.FORBIDDEN));
    }

    private ApiError makeResponse(String message, String reason, HttpStatus status) {
        return new ApiError(message, reason, status, LocalDateTime.now());
    }
}
