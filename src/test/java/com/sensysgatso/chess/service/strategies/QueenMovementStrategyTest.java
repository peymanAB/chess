package com.sensysgatso.chess.service.strategies;

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
import com.sensysgatso.chess.service.impl.strategies.QueenMovementStrategy;
import com.sensysgatso.chess.service.model.MoveRequest;
import com.sensysgatso.chess.service.model.NextMoveModel;

@ExtendWith(SpringExtension.class)
public class QueenMovementStrategyTest {

	@Autowired
	private QueenMovementStrategy queenMovementStrategy;
	
	@Test
	public void isMovementPossible_moveDiagonal_movementIsValid() {
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
				.type(PieceType.QUEEN)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertTrue(queenMovementStrategy.isMovementPossible(moveModel));
	}
	
	@Test
	public void isMovementPossible_moveStraight_movementIsValid() {
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
				.type(PieceType.QUEEN)
				.build());

		GameState lastState = new GameState();
		lastState.setPieces(List.of(moveModel.getSelectedPiece()));
		moveModel.setLastState(lastState);
		assertTrue(queenMovementStrategy.isMovementPossible(moveModel));
	}
	
	@TestConfiguration
	@Import(QueenMovementStrategy.class)
	public static class Configuration{

	}
}
