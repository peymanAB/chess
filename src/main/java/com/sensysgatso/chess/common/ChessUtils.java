package com.sensysgatso.chess.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.SerializationUtils;

import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.ChessPiece.BoardPosition;
import com.sensysgatso.chess.data.GameState;
import com.sensysgatso.chess.data.PieceColor;
import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.service.model.NextMoveModel;

public class ChessUtils {

	private static Map<Integer, PieceType> firstRowTypes = Map.of(
			0 , PieceType.ROOK,
			1 , PieceType.KNIGHT,
			2 , PieceType.BISHOP,
			3 , PieceType.QUEEN,
			4 , PieceType.KING,
			5 , PieceType.BISHOP,
			6 , PieceType.KNIGHT,
			7 , PieceType.ROOK);

	/**
	 * creates a new state based on the last state. use to move the piece in the board.
	 * @param move request model
	 * @return the new state including all of the changes in the board
	 */
	public static GameState generateState(final NextMoveModel move) {
		GameState newState = SerializationUtils.clone(move.getLastState());
		newState.setMoveNumber(move.getLastState().getMoveNumber() + 1);
		newState.setTimestamp(System.currentTimeMillis());
		newState.setUserId(move.getRequest().getUserId());
		
		newState.getPieces().stream()
		.filter(piece -> !piece.getColor().equals(move.getSelectedPiece().getColor()))
		.filter(piece -> piece.getPosition().equals(move.getRequest().getDestination()))
		.findAny().ifPresent(piece -> piece.setRemoved(true));
		
		newState.getPieces().stream().filter(pieceToMove 
				-> pieceToMove.getPieceId().equals(move.getRequest().getPieceId()))
		.findFirst()
		.ifPresent(pieceToMove -> {
			pieceToMove.setPosition(move.getRequest().getDestination());
			pieceToMove.setFirstMove(false);
		});
		return newState;
	}
	
	/**
	 * creates a list of pieces with their initial state in a chess board
	 * @return initiated chess pieces
	 */
	public static List<ChessPiece> getGameInitiationTemplate(){
		List<ChessPiece> pieces = new ArrayList<>();
		pieces.addAll(
				IntStream.range(0, 8).mapToObj(xAxis -> ChessPiece.builder()
						.color(PieceColor.WHITE)
						.firstMove(true)
						.pieceId(UUID.randomUUID().toString())
						.position(new BoardPosition(xAxis, 1))
						.removed(false)
						.type(PieceType.PAWN).build())
				.collect(Collectors.toList())
				);

		pieces.addAll(
				IntStream.range(0, 8).mapToObj(xAxis -> ChessPiece.builder()
						.color(PieceColor.BLACK)
						.firstMove(true)
						.pieceId(UUID.randomUUID().toString())
						.position(new BoardPosition(xAxis, 6))
						.removed(false)
						.type(PieceType.PAWN).build())
				.collect(Collectors.toList())
				);

		pieces.addAll(IntStream.range(0, 8).mapToObj(xAxis -> ChessPiece.builder()
				.color(PieceColor.WHITE)
				.firstMove(true)
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(xAxis, 0))
				.removed(false)
				.type(firstRowTypes.get(xAxis)).build())
				.collect(Collectors.toList())
				);

		pieces.addAll(
				IntStream.range(0, 8).mapToObj(xAxis -> ChessPiece.builder()
						.color(PieceColor.BLACK)
						.firstMove(true)
						.pieceId(UUID.randomUUID().toString())
						.position(new BoardPosition(xAxis, 7))
						.removed(false)
						.type(firstRowTypes.get(xAxis)).build())
				.collect(Collectors.toList())
				);

		return pieces;
	}
}
