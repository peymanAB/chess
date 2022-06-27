package com.sensysgatso.chess.service;

import java.util.Optional;

import com.sensysgatso.chess.api.web.spec.Result;
import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.ChessPiece.BoardPosition;
import com.sensysgatso.chess.exception.InvalidMoveException;
import com.sensysgatso.chess.data.GameState;
import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.service.model.NextMoveModel;

public interface PieceMovementStrategy {

	/**
	 * returns the type that the strategy would support
	 * @return
	 */
	PieceType supportedType();
	
	
	/**
	 * checks whether the movement is possible based on the strategy type
	 * @param move
	 * @return if the movement is possible
	 */
	default boolean isMovementPossible(NextMoveModel move) {
		BoardPosition source = move.getSelectedPiece().getPosition();
		BoardPosition destination = move.getRequest().getDestination();
	
		int verticalMovement = Math.abs(destination.getY() - source.getY());
		int horizontalMovement = Math.abs(destination.getX() - source.getX());
		
		if(verticalMovement == 0 && horizontalMovement == 0) {
			throw new InvalidMoveException("piece is already in the selected position",
					Result.SAME_SOURCE_DESTINATION);
		}
		
		Optional<ChessPiece> targetPiece = move.getLastState().getPieces().stream()
				.filter(piece -> piece.getPosition().equals(move.getRequest().getDestination()))
				.filter(piece -> piece.getColor().equals(move.getSelectedPiece().getColor()))
				.findAny();
		return targetPiece.isPresent() ? false : true;
	}
	
	default boolean isDiagonalMovementPossible(NextMoveModel move) {
		BoardPosition source = move.getSelectedPiece().getPosition();
		BoardPosition destination = move.getRequest().getDestination();
	
		int verticalMovement = Math.abs(destination.getY() - source.getY());
		int horizontalMovement = Math.abs(destination.getX() - source.getX());
		
		if(verticalMovement != horizontalMovement) {
			return false;
		}
		
		int xd = (destination.getX() - source.getX()) / verticalMovement;
		int yd = (destination.getY() - source.getY()) / verticalMovement;
		
		for(int step = 1 ; step < verticalMovement ; step ++) {
			int x = (source.getX() + step * xd);
			int y = (source.getY() + step * yd);
			final BoardPosition nextPosition = new BoardPosition(x, y);
			if(isSquareOccupied(move.getLastState(), nextPosition)) {
				return false;
			}
		}
		
		return true;
	}
	
	default boolean isStraightMovementPossible(NextMoveModel move) {
		BoardPosition source = move.getSelectedPiece().getPosition();
		BoardPosition destination = move.getRequest().getDestination();
		
		int squarePointer = 0;
		int finishPoint = 0;
		
		if(source.getX() == destination.getX()) {
			if(source.getY() > destination.getY()) {
				squarePointer = destination.getY();
				finishPoint = source.getY();
			}else {
				squarePointer = source.getY();
				finishPoint = destination.getY();
			}
			
			squarePointer++;
			while(squarePointer < finishPoint) {
				if(isSquareOccupied(move.getLastState(), new BoardPosition(source.getX(), squarePointer))) {
					return false;
				}
				squarePointer++;
			}
			
			return true;
		}
		
		if(source.getY() == destination.getY()) {
			if(source.getX() > destination.getX()) {
				squarePointer = destination.getX();
				finishPoint = source.getX();
			}else {
				squarePointer = source.getX();
				finishPoint = destination.getX();
			}
			
			squarePointer++;
			while(squarePointer < finishPoint) {
				if(isSquareOccupied(move.getLastState(), new BoardPosition(squarePointer, source.getY()))) {
					return false;
				}
				squarePointer++;
			}
			
			return true;
		}
		
		return false;
	}
	
	default boolean isSquareOccupied(GameState currentState, final BoardPosition position) {
		return currentState.getPieces().stream().anyMatch(piece -> piece.getPosition().equals(position));
	}
}
