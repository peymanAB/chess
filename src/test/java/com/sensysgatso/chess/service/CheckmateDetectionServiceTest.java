package com.sensysgatso.chess.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.PieceColor;
import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.service.impl.CheckmatedetectionServiceImpl;
import com.sensysgatso.chess.service.impl.strategies.KingMovementStrategy;
import com.sensysgatso.chess.data.ChessPiece.BoardPosition;
import com.sensysgatso.chess.data.GameState;

@ExtendWith(SpringExtension.class)
public class CheckmateDetectionServiceTest {

	@Autowired
	private CheckmateDetectionService checkmateDetectionService;
	
	@Test
	public void isKingChecked_KingIsExposed_returnTrue() {
		ChessPiece whiteKing = ChessPiece.builder()
				.color(PieceColor.WHITE)
				.firstMove(false)
				.owner("user1")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(3, 3))
				.removed(false)
				.type(PieceType.KING)
				.build();

		ChessPiece blackRook1 = ChessPiece.builder()
				.color(PieceColor.BLACK)
				.firstMove(false)
				.owner("user2")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(3, 4))
				.removed(false)
				.type(PieceType.ROOK)
				.build();

		ChessPiece blackRook2 = ChessPiece.builder()
				.color(PieceColor.BLACK)
				.firstMove(false)
				.owner("user2")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(2, 2))
				.removed(false)
				.type(PieceType.ROOK)
				.build();
		
		GameState state = new GameState();
		state.setPieces(List.of(whiteKing, blackRook1, blackRook2));
		assertTrue(checkmateDetectionService.isKingCheked(whiteKing, state));
	}
	
	@Test
	public void isCheckmate_KingIsCheckedButCanEscape_returnFalse() {
		ChessPiece whiteKing = ChessPiece.builder()
				.color(PieceColor.WHITE)
				.firstMove(false)
				.owner("user1")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(3, 3))
				.removed(false)
				.type(PieceType.KING)
				.build();

		ChessPiece blackRook1 = ChessPiece.builder()
				.color(PieceColor.BLACK)
				.firstMove(false)
				.owner("user2")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(3, 4))
				.removed(false)
				.type(PieceType.ROOK)
				.build();

		ChessPiece blackRook2 = ChessPiece.builder()
				.color(PieceColor.BLACK)
				.firstMove(false)
				.owner("user2")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(2, 2))
				.removed(false)
				.type(PieceType.ROOK)
				.build();
		
		GameState state = new GameState();
		state.setPieces(List.of(whiteKing, blackRook1, blackRook2));
		state.setMoveNumber(10);
		state.setTimestamp(System.currentTimeMillis());
		state.setUserId("user2");
		assertFalse(checkmateDetectionService.isCheckmate(whiteKing, state));
	}
	
	@Test
	public void isCheckmate_KingIsCheckedAndCannotEscape_returnTrue() {
		ChessPiece whiteKing = ChessPiece.builder()
				.color(PieceColor.WHITE)
				.firstMove(false)
				.owner("user1")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(4, 0))
				.removed(false)
				.type(PieceType.KING)
				.build();

		ChessPiece blackRook1 = ChessPiece.builder()
				.color(PieceColor.BLACK)
				.firstMove(false)
				.owner("user2")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(2, 0))
				.removed(false)
				.type(PieceType.ROOK)
				.build();

		ChessPiece blackRook2 = ChessPiece.builder()
				.color(PieceColor.BLACK)
				.firstMove(false)
				.owner("user2")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(6, 1))
				.removed(false)
				.type(PieceType.ROOK)
				.build();
		
		GameState state = new GameState();
		state.setPieces(List.of(whiteKing, blackRook1, blackRook2));
		state.setMoveNumber(10);
		state.setTimestamp(System.currentTimeMillis());
		state.setUserId("user2");
		assertTrue(checkmateDetectionService.isCheckmate(whiteKing, state));
	}
	
	@TestConfiguration
	@Import(CheckmatedetectionServiceImpl.class)
	@ComponentScan(basePackageClasses = KingMovementStrategy.class)
	public static class Configuration{

	}
}
