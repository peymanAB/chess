package com.sensysgatso.chess.service.impl.strategies;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.ChessPiece.BoardPosition;
import com.sensysgatso.chess.data.PieceColor;
import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.service.PieceMovementStrategy;
import com.sensysgatso.chess.service.model.NextMoveModel;

@Service
public class PawnMovementStrategy implements PieceMovementStrategy{

	@Override
	public boolean isMovementPossible(NextMoveModel move) {
		if(!PieceMovementStrategy.super.isMovementPossible(move)) {
			return false;
		}	
		BoardPosition source = move.getSelectedPiece().getPosition();
		BoardPosition destination = move.getRequest().getDestination();
		
		Optional<ChessPiece> targetPiece = move.getLastState().getPieces()
				.stream().filter(piece -> piece.getPosition().equals(destination))
				.findFirst();
		
		int verticalMovement = destination.getY() - source.getY();
		int horizontalMovement = destination.getX() - source.getX();
		
		int oneStep = move.getSelectedPiece().getColor().equals(PieceColor.WHITE) ? 1 : -1;
		int twoStep = move.getSelectedPiece().getColor().equals(PieceColor.WHITE) ? 2 : -2;
		
		if(verticalMovement == oneStep) {
			if(horizontalMovement == 0 && targetPiece.isEmpty()) {
				return true;
			}else if(horizontalMovement == oneStep && targetPiece.isPresent() 
					&& targetPiece.get().getColor() != move.getSelectedPiece().getColor()) {
				return true;
			}else {
				return false;
			}
		}else if(verticalMovement == twoStep) {
			if(move.getSelectedPiece().isFirstMove() && targetPiece.isEmpty()) {
				return true;
			}
			return false;
		}else {
			return false;
		}
	}
	
	@Override
	public PieceType supportedType() {
		return PieceType.PAWN;
	}
}
