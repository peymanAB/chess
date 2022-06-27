package com.sensysgatso.chess.service.model;

import com.sensysgatso.chess.data.ChessGame;
import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.GameState;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class NextMoveModel {

	private MoveRequest request;
	
	private ChessGame game;
	
	private GameState lastState;
	
	private ChessPiece selectedPiece;

}
