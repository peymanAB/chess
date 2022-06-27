package com.sensysgatso.chess.api.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sensysgatso.chess.api.web.spec.GeneralResponse;
import com.sensysgatso.chess.api.web.spec.Result;
import com.sensysgatso.chess.exception.GameNotFoundException;
import com.sensysgatso.chess.exception.InvalidMoveException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ResourceExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(InvalidMoveException.class)
	public ResponseEntity<GeneralResponse> handleMovementException(InvalidMoveException ex, WebRequest request){
		log.debug("movement request failed due to error = ", ex);
		GeneralResponse response = new GeneralResponse();
		response.setStatus(ex.getProblem());
		response.setMsg(ex.getMessage());
		return ResponseEntity.unprocessableEntity().body(response);
	}
	
	@ExceptionHandler(GameNotFoundException.class)
	public ResponseEntity<GeneralResponse> handleMovementException(GameNotFoundException ex, WebRequest request){
		log.debug("there is no active game with the requested id ", ex);
		GeneralResponse response = new GeneralResponse();
		response.setStatus(Result.GAME_NOT_FOUND);
		response.setMsg(ex.getMessage());
		return ResponseEntity.unprocessableEntity().body(response);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<GeneralResponse> handleGeneralException(Exception ex, WebRequest request){
		log.error("request failed with error = ", ex);
		GeneralResponse response = new GeneralResponse();
		response.setStatus(Result.INTERNAL_ERROR);
		response.setMsg(ex.getMessage());
		return ResponseEntity.internalServerError().body(response);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.debug("request failed with invalid params", ex);
		GeneralResponse response = new GeneralResponse();
		response.setStatus(Result.BAD_REQUEST);
		response.setMsg(ex.getFieldError().getDefaultMessage());
		return ResponseEntity.badRequest().body(response);
	}
	
}
