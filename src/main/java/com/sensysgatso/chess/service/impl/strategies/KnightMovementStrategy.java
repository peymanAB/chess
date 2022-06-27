package com.sensysgatso.chess.service.impl.strategies;

import com.sensysgatso.chess.data.ChessPiece.BoardPosition;

import org.springframework.stereotype.Service;

import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.service.PieceMovementStrategy;
import com.sensysgatso.chess.service.model.NextMoveModel;

@Service
public class KnightMovementStrategy implements PieceMovementStrategy{

	@Override
	public boolean isMovementPossible(NextMoveModel move) {
		if(!PieceMovementStrategy.super.isMovementPossible(move)) {
			return false;
		}	
		BoardPosition source = move.getSelectedPiece().getPosition();
		BoardPosition destination = move.getRequest().getDestination();
		
		int verticalMovement = Math.abs(destination.getY() - source.getY());
		int horizontalMovement = Math.abs(destination.getX() - source.getX());
		
		if((horizontalMovement == 2 && verticalMovement == 1) ||
				(horizontalMovement == 1 && verticalMovement == 2)) {
			return true;
		}else {
			return false;
		}
	}
	
	@Override
	public PieceType supportedType() {
		return PieceType.KNIGHT;
	}
}
