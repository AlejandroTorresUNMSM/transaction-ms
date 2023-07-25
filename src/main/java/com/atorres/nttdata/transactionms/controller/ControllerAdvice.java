package com.atorres.nttdata.transactionms.controller;

import com.atorres.nttdata.transactionms.exception.CustomException;
import com.atorres.nttdata.transactionms.exception.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@RestControllerAdvice
public class ControllerAdvice {
    /**
     * Metodo que captura una excepcion personalizada
     * @param ex excepcion
     * @return errordto
     */
    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ErrorDto> customExceptionHandler(CustomException ex){
        ErrorDto error =  new ErrorDto();
        error.setHttpStatus(ex.getStatus());
        error.setMessage(ex.getMessage());
        return new ResponseEntity<>(error,ex.getStatus());
    }

    /**
     * Metodo por si falla al conectarse el client Feign
     * @param ex excepcion
     * @return errordto
     */
    @ExceptionHandler(value = WebClientRequestException.class)
    public ResponseEntity<ErrorDto> webClientResponseHandler(WebClientRequestException ex){
        ErrorDto error =  new ErrorDto();
        String errorMessage = "Error en la solicitud: " + ex.getMessage();
        error.setHttpStatus(HttpStatus.BAD_REQUEST);
        error.setMessage(errorMessage);
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

}
