package com.sensysgatso.chess.service.strategies;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.GameState;
import com.sensysgatso.chess.data.PieceColor;
import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.data.ChessPiece.BoardPosition;
import com.sensysgatso.chess.service.impl.strategies.RookMovementStrategy;
import com.sensysgatso.chess.service.model.MoveRequest;
import com.sensysgatso.chess.service.model.NextMoveModel;

@ExtendWith(SpringExtension.class)
public class RookMovementStrategyTest {

	@Autowired
	private RookMovementStrategy rookMovementStrategy;
	
	@Test
	public void isMovementPossible_moveToHigherXWithoutBarrier_movementIsValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(5, 2))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(1, 2))
				.type(PieceType.ROOK)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertTrue(rookMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_moveToHigherXWithBarrier_movementIsNotValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(5, 2))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(1, 2))
				.type(PieceType.BISHOP)
				.build());
		
		ChessPiece targetPiece = ChessPiece.builder()
				.pieceId(pieceId)
				.firstMove(false)
				.position(new BoardPosition(3, 2))
				.type(PieceType.KNIGHT)
				.build();

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece(), targetPiece));
		moveModel.setLastState(lastState);
		assertFalse(rookMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_moveToHigherYWithoutBarrier_movementIsValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(1, 5))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(1, 2))
				.type(PieceType.ROOK)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertTrue(rookMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_moveToHigherYWithBarrier_movementIsNotValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(1, 5))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(1, 2))
				.type(PieceType.BISHOP)
				.build());
		
		ChessPiece targetPiece = ChessPiece.builder()
				.pieceId(pieceId)
				.firstMove(false)
				.position(new BoardPosition(1, 3))
				.type(PieceType.KNIGHT)
				.build();

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece(), targetPiece));
		moveModel.setLastState(lastState);
		assertFalse(rookMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_movementIsNotStraight_movementIsNotValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(5, 3))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(1, 2))
				.type(PieceType.ROOK)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertFalse(rookMovementStrategy.isMovementPossible(moveModel));
	}
	
	@TestConfiguration
	@Import(RookMovementStrategy.class)
	public static class Configuration{

	}
}
