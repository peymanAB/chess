package com.sensysgatso.chess.exception;

public class GameNotFoundException extends IllegalStateException{

	private static final long serialVersionUID = -26871340342747176L;

	public GameNotFoundException(String msg) {
		super(msg);
	}
}
