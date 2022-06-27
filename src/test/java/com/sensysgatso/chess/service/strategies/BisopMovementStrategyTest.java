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
import com.sensysgatso.chess.data.ChessPiece.BoardPosition;
import com.sensysgatso.chess.data.GameState;
import com.sensysgatso.chess.data.PieceColor;
import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.service.impl.strategies.BishopMovementStrategy;
import com.sensysgatso.chess.service.model.MoveRequest;
import com.sensysgatso.chess.service.model.NextMoveModel;

@ExtendWith(SpringExtension.class)
public class BisopMovementStrategyTest {

	@Autowired
	private BishopMovementStrategy bishopMovementStrategy;
	
	@Test
	public void isMovementPossible_moveToHigherXandYWithoughBarrier_movementIsValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(7, 4))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(5, 2))
				.type(PieceType.BISHOP)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertTrue(bishopMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_movementIsNotDiagonal_movementIsNotValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(7, 5))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(5, 2))
				.type(PieceType.BISHOP)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertFalse(bishopMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_moveToHigherXandYWithBarrier_movementIsNotValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(7, 4))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(5, 2))
				.type(PieceType.BISHOP)
				.build());
		
		ChessPiece targetPiece = ChessPiece.builder()
				.pieceId(pieceId)
				.firstMove(false)
				.position(new BoardPosition(6, 3))
				.type(PieceType.KNIGHT)
				.build();

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece(), targetPiece));
		moveModel.setLastState(lastState);
		assertFalse(bishopMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_moveToLowerXandYWithoughBarrier_movementIsValid() {
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
				.position(new BoardPosition(7, 4))
				.type(PieceType.BISHOP)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertTrue(bishopMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_moveToLowerXandYWithBarrier_movementIsNotValid() {
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
				.position(new BoardPosition(7, 4))
				.type(PieceType.BISHOP)
				.build());
		
		ChessPiece targetPiece = ChessPiece.builder()
				.pieceId(pieceId)
				.firstMove(false)
				.position(new BoardPosition(6, 3))
				.type(PieceType.KNIGHT)
				.build();

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece(), targetPiece));
		moveModel.setLastState(lastState);
		assertFalse(bishopMovementStrategy.isMovementPossible(moveModel));
	}
	
	@TestConfiguration
	@Import(BishopMovementStrategy.class)
	public static class Configuration{

	}
}
