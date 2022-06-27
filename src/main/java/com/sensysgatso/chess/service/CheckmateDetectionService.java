package com.sensysgatso.chess.service;

import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.GameState;

public interface CheckmateDetectionService {

	/**
	 * checks to see if the king is in the checked situation
	 * @param king to check
	 * @param state updated board state
	 * @return the check status
	 */
	boolean isKingCheked(ChessPiece king, GameState state);
	
	/**
	 * checks the checkmate situation
	 * @param king to check
	 * @param state updated board state
	 * @return the checkmate status
	 */
	boolean isCheckmate(ChessPiece king, GameState state);
}
