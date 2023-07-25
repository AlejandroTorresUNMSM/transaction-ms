package com.atorres.nttdata.transactionms.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorDto {
	private HttpStatus httpStatus;
	private String message;
}
