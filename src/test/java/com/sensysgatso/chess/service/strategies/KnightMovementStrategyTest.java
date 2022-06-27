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
import com.sensysgatso.chess.service.impl.strategies.KnightMovementStrategy;
import com.sensysgatso.chess.service.model.MoveRequest;
import com.sensysgatso.chess.service.model.NextMoveModel;

@ExtendWith(SpringExtension.class)
public class KnightMovementStrategyTest {

	@Autowired
	private KnightMovementStrategy knightMovementStrategy;
	
	@Test
	public void isMovementPossible_oneStepVerticalTwoStepHorizontal_movementIsValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(6, 4))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(4, 3))
				.type(PieceType.KNIGHT)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertTrue(knightMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_twoStepVerticalOneStepHorizontal_movementIsValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(5, 5))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(4, 3))
				.type(PieceType.KNIGHT)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertTrue(knightMovementStrategy.isMovementPossible(moveModel));
	}

	@Test
	public void isMovementPossible_twoStepVerticalTwoStepHorizontal_movementIsNotValid() {
		String pieceId = UUID.randomUUID().toString();
		NextMoveModel moveModel = new NextMoveModel();
		moveModel.setRequest(MoveRequest.builder()
				.pieceId(pieceId)
				.destination(new BoardPosition(6, 5))
				.build());
		moveModel.setSelectedPiece(ChessPiece.builder()
				.pieceId(pieceId).
				color(PieceColor.WHITE)
				.firstMove(false)
				.position(new BoardPosition(4, 3))
				.type(PieceType.KNIGHT)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertFalse(knightMovementStrategy.isMovementPossible(moveModel));
	}
	
	@TestConfiguration
	@Import(KnightMovementStrategy.class)
	public static class Configuration{

	}
}
