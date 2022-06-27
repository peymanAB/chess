package com.sensysgatso.chess.service.impl.strategies;

import org.springframework.stereotype.Service;

import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.service.PieceMovementStrategy;
import com.sensysgatso.chess.service.model.NextMoveModel;

@Service
public class BishopMovementStrategy implements PieceMovementStrategy{

	@Override
	public boolean isMovementPossible(NextMoveModel move) {
		if(!PieceMovementStrategy.super.isMovementPossible(move)) {
			return false;
		}		
		return isDiagonalMovementPossible(move);
	}

	@Override
	public PieceType supportedType() {
		return PieceType.BISHOP;
	}
}
