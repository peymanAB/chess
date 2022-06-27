package com.sensysgatso.chess.service;

import java.util.List;

import com.sensysgatso.chess.data.ChessGame;
import com.sensysgatso.chess.service.model.NextMoveModel;

public interface GameService {

	/**
	 * initiates a new game with the given participants
	 * @param participants
	 * @return initial state of a chess board game
	 */
	ChessGame initiate(List<String> participants);
	
	/** moves the requested piece ( if possible ) and returns the new state
	 * @param nextMoveModel
	 * @return the board state after the move is successfully done
	 */
	ChessGame move(NextMoveModel nextMoveModel);
}
