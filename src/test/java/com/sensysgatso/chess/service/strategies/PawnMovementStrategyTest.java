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
import com.sensysgatso.chess.service.impl.strategies.PawnMovementStrategy;
import com.sensysgatso.chess.service.model.MoveRequest;
import com.sensysgatso.chess.service.model.NextMoveModel;

@ExtendWith(SpringExtension.class)
public class PawnMovementStrategyTest {

	@Autowired
	private PawnMovementStrategy pawnMovementStrategy;

	@Test
	public void isMovementPossible_oneStepVerticalMovement_movementIsValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(4, 4))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(4, 3))
				.type(PieceType.PAWN)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertTrue(pawnMovementStrategy.isMovementPossible(moveModel));
	}

	@Test
	public void isMovementPossible_oneStepVerticalToOccupiedPlace_movementIsNotValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(4, 4))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(4, 3))
				.type(PieceType.PAWN)
				.build());

		ChessPiece targetPiece = ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(4, 4))
				.type(PieceType.PAWN)
				.build();

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece(), targetPiece));
		moveModel.setLastState(lastState);
		assertFalse(pawnMovementStrategy.isMovementPossible(moveModel));
	}

	@Test
	public void isMovementPossible_oneStepDiagonalMovementWithoutTarget_movementIsNotValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(5, 4))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(4, 3))
				.type(PieceType.PAWN)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertFalse(pawnMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_oneStepDiagonalToOccupiedRival_movementIsValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(5, 4))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(4, 3))
				.type(PieceType.PAWN)
				.build());
		
		ChessPiece targetPiece = ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.BLACK)
				.firstMove(false)
				.position(new BoardPosition(5, 4))
				.type(PieceType.BISHOP)
				.build();

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece(), targetPiece));
		moveModel.setLastState(lastState);
		assertTrue(pawnMovementStrategy.isMovementPossible(moveModel));
	}

	@Test
	public void isMovementPossible_twoStepVerticalMovementOnFirstMove_movementIsValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(4, 3))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(true)
				.position(new BoardPosition(4, 1))
				.type(PieceType.PAWN)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertTrue(pawnMovementStrategy.isMovementPossible(moveModel));
	}

	@Test
	public void isMovementPossible_threeStepVerticalMovement_movementIsNotValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(4, 4))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(true)
				.position(new BoardPosition(4, 1))
				.type(PieceType.PAWN)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertFalse(pawnMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_oneStepBackward_movementIsNotValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(5, 1))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(true)
				.position(new BoardPosition(5, 2))
				.type(PieceType.PAWN)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertFalse(pawnMovementStrategy.isMovementPossible(moveModel));
	}

	@TestConfiguration
	@Import(PawnMovementStrategy.class)
	public static class Configuration{

	}
}
