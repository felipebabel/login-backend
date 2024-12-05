package com.securityspring.exception.handler;

import com.securityspring.util.DefaultResponse;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@AllArgsConstructor(onConstructor_ = @Autowired)
@ControllerAdvice
public class ResponseExceptionHandler {

    static final Logger LOGGER = LoggerFactory.getLogger("ResponseExceptionHandler");

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleAllExceptions(final BadRequestException exception,
                                                            final WebRequest request) {
        ;
        return new ResponseEntity<>(DefaultResponse.builder()
                .message(exception.getMessage())
                .status("ERROR").build(), HttpStatus.BAD_REQUEST);
    }

}
