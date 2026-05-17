package br.imd.ufrn.egide.controller;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import br.imd.ufrn.egide.dto.ErrorMessage;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;

// Handler global de exceções da API; intercepta todas as exceções não tratadas nos controllers
// e retorna respostas HTTP padronizadas no formato ErrorMessage.
@ControllerAdvice
public class HandlerGlobalException {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorMessage message = ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorMessage> businessException(BusinessException ex, WebRequest request) {
        ErrorMessage message = ErrorMessage.builder()
                .statusCode(ex.getHttpStatusCode().value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();

        return new ResponseEntity<>(message, ex.getHttpStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorMessage message = ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .message("Validation Error")
                .description(request.getDescription(false))
                .errors(errors)
                .build();

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex, WebRequest request) {
        ErrorMessage message = ErrorMessage.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .description(request.getDescription(false))
                .build();

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
