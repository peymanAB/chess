package com.sensysgatso.chess.service;

import com.sensysgatso.chess.exception.InvalidMoveException;
import com.sensysgatso.chess.service.model.NextMoveModel;

public interface MovementValidator {

	/**
	 * validates the possibility of the movement 
	 * @param nextMoveModel the movement request model
	 * @throws InvalidMoveException in case of an invalid movement request
	 */
	void validateMove(final NextMoveModel nextMoveModel) throws InvalidMoveException;
	
	/**
	 * shows the order in which the validation would run
	 * @return
	 */
	int order();
}
