package com.sensysgatso.chess.exception;

import com.sensysgatso.chess.api.web.spec.Result;

import lombok.Getter;

@Getter
public class InvalidMoveException extends IllegalStateException{

	private static final long serialVersionUID = -5371102916889371124L;

	private final Result problem;
	
	public InvalidMoveException(String msg, Result problem) {
		super(msg);
		this.problem = problem;
	}
	
}
