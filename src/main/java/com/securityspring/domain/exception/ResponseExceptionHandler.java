package com.securityspring.domain.exception;

import java.util.stream.Collectors;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@AllArgsConstructor(onConstructor_ = @Autowired)
@ControllerAdvice
public class ResponseExceptionHandler {

    static final Logger LOGGER = LoggerFactory.getLogger(ResponseExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<DefaultResponse> handleBadRequest(BadRequestException ex, WebRequest request) {
        LOGGER.error("BadRequestException: {}", ex.getMessage(), ex);
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOGGER.error("Validation failed: {}", ex.getMessage(), ex);
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return buildResponse(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserNotFoundException.class, ConfigNotFoundException.class, TokenNotValidException.class})
    public ResponseEntity<DefaultResponse> handleUserNotFound(RuntimeException ex) {
        LOGGER.error("{}: {}", ex.getClass(), ex.getMessage(), ex);
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserPendingException.class, UserBlockedException.class, UserInactiveException.class, InvalidPasswordException.class,
            UserAlreadyExistsException.class})
    public ResponseEntity<DefaultResponse> handleUserExceptions(RuntimeException ex) {
        LOGGER.error("{}: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<DefaultResponse> buildResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(DefaultResponse.builder()
                .message(message)
                .status(DefaultResponse.ERROR)
                .build(), status);
    }
}
