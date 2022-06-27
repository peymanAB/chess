package com.sensysgatso.chess.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sensysgatso.chess.api.web.spec.Result;
import com.sensysgatso.chess.data.ChessGame;
import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.ChessPiece.BoardPosition;
import com.sensysgatso.chess.data.GameRepository;
import com.sensysgatso.chess.data.GameState;
import com.sensysgatso.chess.data.PieceColor;
import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.exception.GameNotFoundException;
import com.sensysgatso.chess.exception.InvalidMoveException;
import com.sensysgatso.chess.service.model.MoveRequest;
import com.sensysgatso.chess.service.model.NextMoveModel;

@ExtendWith(SpringExtension.class)
public class GameServiceTest {

	@Autowired
	private GameService gameService;

	@MockBean
	private GameRepository gameRepository;

	@Test
	public void move_gameDoesNotExist_throwGameNotFound() {
		String gameId = UUID.randomUUID().toString();
		doReturn(Optional.empty()).when(gameRepository).findByUid(gameId);
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder().gameId(gameId).build());
		assertThrows(GameNotFoundException.class, () -> gameService.move(model));
	}

	@Test
	public void move_gameAlreadyFinished_gameNotFoundException() {
		String gameId = UUID.randomUUID().toString();
		ChessGame game = new ChessGame();
		game.setId(gameId);
		game.setFinished(true);
		doReturn(Optional.of(game)).when(gameRepository).findByUid(gameId);
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder().gameId(gameId).build());
		assertThrows(GameNotFoundException.class, () -> gameService.move(model));
	}

	@Test
	@DisplayName("when the request piece is removed, return invalid move exception with PIECE_REMOVED result")
	public void move_requestedPieceIsRemoved_InvalidMoveException() {
		String gameId = UUID.randomUUID().toString();
		String pieceId = UUID.randomUUID().toString();
		ChessGame game = new ChessGame();
		game.setId(gameId);
		GameState state = new GameState();
		state.setPieces(List.of(ChessPiece.builder().pieceId(pieceId).removed(true).build()));
		game.setStates(List.of(state));
		doReturn(Optional.of(game)).when(gameRepository).findByUid(gameId);
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder().gameId(gameId).pieceId(pieceId).build());
		InvalidMoveException exception = assertThrows(InvalidMoveException.class, () -> gameService.move(model));
		assertEquals(Result.PIECE_REMOVED, exception.getProblem());
	}

	@Test
	@DisplayName("when the request piece is not owned by user, return invalid move exception with PIECE_OWNERSHIP result")
	public void move_requestedPieceDoesNotBelongToUser_InvalidMoveException() {
		String gameId = UUID.randomUUID().toString();
		String pieceId = UUID.randomUUID().toString();
		ChessGame game = new ChessGame();
		game.setId(gameId);
		GameState state = new GameState();
		state.setPieces(List.of(ChessPiece.builder().pieceId(pieceId).removed(false).owner("user2").build()));
		game.setStates(List.of(state));
		doReturn(Optional.of(game)).when(gameRepository).findByUid(gameId);
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder().gameId(gameId).pieceId(pieceId).userId("user1").build());
		InvalidMoveException exception = assertThrows(InvalidMoveException.class, () -> gameService.move(model));
		assertEquals(Result.PIECE_OWNERSHIP, exception.getProblem());
	}


	@Test
	@DisplayName("when the requested destination is out of X upper bound,return invalid move exception with OUT_OF_BOUND_DESTINATION result")
	public void move_outOfUpperXBoundDestination_InvalidMoveException() {
		String gameId = UUID.randomUUID().toString();
		String pieceId = UUID.randomUUID().toString();
		ChessGame game = new ChessGame();
		game.setId(gameId);
		GameState state = new GameState();
		state.setPieces(List.of(ChessPiece.builder().pieceId(pieceId).removed(false).owner("user1").build()));
		game.setStates(List.of(state));
		doReturn(Optional.of(game)).when(gameRepository).findByUid(gameId);
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder().gameId(gameId)
				.pieceId(pieceId).userId("user1")
				.destination(new BoardPosition(8 , 2)).build());
		InvalidMoveException exception = assertThrows(InvalidMoveException.class, () -> gameService.move(model));
		assertEquals(Result.OUT_OF_BOUND_DESTINATION, exception.getProblem());
	}

	@Test
	@DisplayName("when the requested destination is out of Y upper bound,return invalid move exception with OUT_OF_BOUND_DESTINATION result")
	public void move_outOfUpperYBoundDestination_InvalidMoveException() {
		String gameId = UUID.randomUUID().toString();
		String pieceId = UUID.randomUUID().toString();
		ChessGame game = new ChessGame();
		game.setId(gameId);
		GameState state = new GameState();
		state.setPieces(List.of(ChessPiece.builder().pieceId(pieceId).removed(false).owner("user1").build()));
		game.setStates(List.of(state));
		doReturn(Optional.of(game)).when(gameRepository).findByUid(gameId);
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder().gameId(gameId)
				.pieceId(pieceId).userId("user1")
				.destination(new BoardPosition(2 , 8)).build());
		InvalidMoveException exception = assertThrows(InvalidMoveException.class, () -> gameService.move(model));
		assertEquals(Result.OUT_OF_BOUND_DESTINATION, exception.getProblem());
	}

	@Test
	@DisplayName("when the requested destination is out of X lower bound,return invalid move exception with OUT_OF_BOUND_DESTINATION result")
	public void move_outOfLowerXBoundDestination_InvalidMoveException() {
		String gameId = UUID.randomUUID().toString();
		String pieceId = UUID.randomUUID().toString();
		ChessGame game = new ChessGame();
		game.setId(gameId);
		GameState state = new GameState();
		state.setPieces(List.of(ChessPiece.builder().pieceId(pieceId).removed(false).owner("user1").build()));
		game.setStates(List.of(state));
		doReturn(Optional.of(game)).when(gameRepository).findByUid(gameId);
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder().gameId(gameId)
				.pieceId(pieceId).userId("user1")
				.destination(new BoardPosition(-1 , 2)).build());
		InvalidMoveException exception = assertThrows(InvalidMoveException.class, () -> gameService.move(model));
		assertEquals(Result.OUT_OF_BOUND_DESTINATION, exception.getProblem());
	}

	@Test
	@DisplayName("when the requested destination is out of Y lower bound,return invalid move exception with OUT_OF_BOUND_DESTINATION result")
	public void move_outOfLowerYBoundDestination_InvalidMoveException() {
		String gameId = UUID.randomUUID().toString();
		String pieceId = UUID.randomUUID().toString();
		ChessGame game = new ChessGame();
		game.setId(gameId);
		GameState state = new GameState();
		state.setPieces(List.of(ChessPiece.builder().pieceId(pieceId).removed(false).owner("user1").build()));
		game.setStates(List.of(state));
		doReturn(Optional.of(game)).when(gameRepository).findByUid(gameId);
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder().gameId(gameId)
				.pieceId(pieceId).userId("user1")
				.destination(new BoardPosition(2 , -1)).build());
		InvalidMoveException exception = assertThrows(InvalidMoveException.class, () -> gameService.move(model));
		assertEquals(Result.OUT_OF_BOUND_DESTINATION, exception.getProblem());
	}

	@Test
	@DisplayName("when the first move is made by black player,return invalid move exception with OUT_OF_TURN_MOVEMENT result")
	public void move_firstMoveByBlackPlayer_InvalidMoveException() {
		String gameId = UUID.randomUUID().toString();
		String pieceId = UUID.randomUUID().toString();
		ChessGame game = new ChessGame();
		game.setId(gameId);
		GameState state = new GameState();
		state.setMoveNumber(0);
		state.setPieces(List.of(ChessPiece.builder().pieceId(pieceId)
				.removed(false).owner("user1").color(PieceColor.BLACK).build()));
		game.setStates(List.of(state));
		doReturn(Optional.of(game)).when(gameRepository).findByUid(gameId);
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder().gameId(gameId)
				.pieceId(pieceId).userId("user1")
				.destination(new BoardPosition(3, 5)).build());
		InvalidMoveException exception = assertThrows(InvalidMoveException.class, () -> gameService.move(model));
		assertEquals(Result.OUT_OF_TURN_MOVEMENT, exception.getProblem());
	}

	@Test
	@DisplayName("when the user moved out of turn,return invalid move exception with OUT_OF_TURN_MOVEMENT result")
	public void move_userMovedOutOfTurn_InvalidMoveException() {
		String gameId = UUID.randomUUID().toString();
		String pieceId = UUID.randomUUID().toString();
		ChessGame game = new ChessGame();
		game.setId(gameId);
		GameState state = new GameState();
		state.setMoveNumber(6);
		state.setUserId("user1");
		state.setPieces(List.of(ChessPiece.builder().pieceId(pieceId)
				.removed(false).owner("user1").color(PieceColor.BLACK).build()));
		game.setStates(List.of(state));
		doReturn(Optional.of(game)).when(gameRepository).findByUid(gameId);
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder().gameId(gameId)
				.pieceId(pieceId).userId("user1")
				.destination(new BoardPosition(3, 5)).build());
		InvalidMoveException exception = assertThrows(InvalidMoveException.class, () -> gameService.move(model));
		assertEquals(Result.OUT_OF_TURN_MOVEMENT, exception.getProblem());
	}

	@Test
	@DisplayName("when the move would endanger the king, return invalid move exception with KING_EXPOSURE result")
	public void move_moveCauseKingExposure_InvalidMoveException() {
		String gameId = UUID.randomUUID().toString();
		ChessGame game = new ChessGame();
		game.setId(gameId);
		GameState state = new GameState();
		state.setMoveNumber(14);
		state.setUserId("user1");

		ChessPiece whiteKing = ChessPiece.builder()
				.color(PieceColor.WHITE)
				.firstMove(false)
				.owner("user2")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(3, 3))
				.removed(false)
				.type(PieceType.KING)
				.build();


		ChessPiece whiteRook = ChessPiece.builder()
				.color(PieceColor.WHITE)
				.firstMove(false)
				.owner("user2")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(3, 4))
				.removed(false)
				.type(PieceType.ROOK)
				.build();


		ChessPiece blackRook = ChessPiece.builder()
				.color(PieceColor.BLACK)
				.firstMove(false)
				.owner("user1")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(3, 5))
				.removed(false)
				.type(PieceType.ROOK)
				.build();

		state.setPieces(List.of(whiteKing, whiteRook, blackRook));
		game.setStates(List.of(state));
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder()
				.pieceId(whiteRook.getPieceId())
				.gameId(gameId)
				.destination(new BoardPosition(5, 4))
				.userId("user2").build());
		model.setGame(game);
		model.setLastState(state);
		model.setSelectedPiece(whiteRook);
		doReturn(Optional.of(game)).when(gameRepository).findByUid(gameId);
		InvalidMoveException exception = assertThrows(InvalidMoveException.class, () -> gameService.move(model));
		assertEquals(Result.KING_EXPOSURE, exception.getProblem());
	}

	@Test
	public void move_moveIsValidAndDestinationIsOccupiedWithOponent_removeOponent() {
		String gameId = UUID.randomUUID().toString();
		ChessGame game = new ChessGame();
		game.setId(gameId);
		GameState state = new GameState();
		state.setMoveNumber(14);
		state.setUserId("user1");

		ChessPiece whiteKing = ChessPiece.builder()
				.color(PieceColor.WHITE)
				.firstMove(false)
				.owner("user2")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(3, 3))
				.removed(false)
				.type(PieceType.KING)
				.build();

		ChessPiece blackKing = ChessPiece.builder()
				.color(PieceColor.BLACK)
				.firstMove(false)
				.owner("user1")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(1,2))
				.removed(false)
				.type(PieceType.KING)
				.build();

		ChessPiece whiteRook = ChessPiece.builder()
				.color(PieceColor.WHITE)
				.firstMove(false)
				.owner("user2")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(3, 4))
				.removed(false)
				.type(PieceType.ROOK)
				.build();

		ChessPiece blackRook = ChessPiece.builder()
				.color(PieceColor.BLACK)
				.firstMove(false)
				.owner("user1")
				.pieceId(UUID.randomUUID().toString())
				.position(new BoardPosition(3, 5))
				.removed(false)
				.type(PieceType.ROOK)
				.build();

		state.setPieces(List.of(blackKing, whiteKing, whiteRook, blackRook));
		List<GameState> states = new ArrayList<>();
		states.add(state);
		game.setStates(states);
		NextMoveModel model = new NextMoveModel();
		model.setRequest(MoveRequest.builder()
				.pieceId(whiteRook.getPieceId())
				.gameId(gameId)
				.destination(new BoardPosition(3, 5))
				.userId("user2").build());
		model.setGame(game);
		model.setLastState(state);
		model.setSelectedPiece(whiteRook);
		doReturn(Optional.of(game)).when(gameRepository).findByUid(gameId);
		Mockito.when(gameRepository.save(any())).then(AdditionalAnswers.returnsFirstArg());
		ChessGame result = gameService.move(model);
		assertEquals(2, result.getStates().size());
		assertEquals(15, result.getStates().get(1).getMoveNumber());
		assertEquals("user2", result.getStates().get(1).getUserId());
		assertNotNull(result.getStates().get(1).getTimestamp());

		ChessPiece removedBlackRook = result.getStates().get(1).getPieces()
				.stream().filter(piece -> piece.getPieceId().equals(blackRook.getPieceId()))
				.findFirst().get();
		assertTrue(removedBlackRook.isRemoved());
	}

	@Test
	public void initiate_gameDataIsValid() {
		List<String> users = new ArrayList<>();
		users.add("user1");
		users.add("user2");
		Mockito.when(gameRepository.save(any())).then(AdditionalAnswers.returnsFirstArg());
		ChessGame initiatedGame = gameService.initiate(users);
		assertNotNull(initiatedGame.getUid());
		assertNotNull(initiatedGame.getCreationDate());
		assertNull(initiatedGame.getFinishTime());
		assertEquals(32,initiatedGame.getStates()
				.get(0).getPieces().size());

		initiatedGame.getStates().get(0).getPieces().forEach(piece ->{
			assertNotNull(piece.getColor());
			assertNotNull(piece.getOwner());
			assertNotNull(piece.getPieceId());
			assertNotNull(piece.getPosition());
			assertNotNull(piece.getType());
		});
	}

	@Test
	public void initiate_whitePiecesAreValid() {
		List<String> users = new ArrayList<>();
		users.add("user1");
		users.add("user2");
		Mockito.when(gameRepository.save(any())).then(AdditionalAnswers.returnsFirstArg());
		ChessGame initiatedGame = gameService.initiate(users);

		List<ChessPiece> whitePawns = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.PAWN)
				.filter(piece -> piece.getColor().equals(PieceColor.WHITE))
				.collect(Collectors.toList());

		assertEquals(8, whitePawns.size());
		whitePawns.forEach(piece -> assertEquals(1, piece.getPosition().getY()));
		List<Integer> whitePawnsPlace = whitePawns.stream().map(ChessPiece::getPosition)
				.map(BoardPosition::getX).collect(Collectors.toList());
		assertTrue(whitePawnsPlace.containsAll(List.of(0, 1, 2, 3, 4, 5, 6, 7)));

		ChessPiece whiteKing = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.KING)
				.filter(piece -> piece.getColor().equals(PieceColor.WHITE)).findFirst().get();
		assertEquals(new BoardPosition(4, 0), whiteKing.getPosition());

		ChessPiece whiteQueen = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.QUEEN)
				.filter(piece -> piece.getColor().equals(PieceColor.WHITE)).findFirst().get();
		assertEquals(new BoardPosition(3, 0), whiteQueen.getPosition());

		List<ChessPiece> rooks = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.ROOK)
				.filter(piece -> piece.getColor().equals(PieceColor.WHITE))
				.collect(Collectors.toList());
		assertEquals(2, rooks.size());
		assertTrue(rooks.stream().map(ChessPiece::getPosition)
				.toList().containsAll(List.of(new BoardPosition(0,0), new BoardPosition(7, 0))));

		List<ChessPiece> knights = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.KNIGHT)
				.filter(piece -> piece.getColor().equals(PieceColor.WHITE))
				.collect(Collectors.toList());
		assertEquals(2, knights.size());
		assertTrue(knights.stream().map(ChessPiece::getPosition)
				.toList().containsAll(List.of(new BoardPosition(1,0), new BoardPosition(6, 0))));

		List<ChessPiece> bishops = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.BISHOP)
				.filter(piece -> piece.getColor().equals(PieceColor.WHITE))
				.collect(Collectors.toList());
		assertEquals(2, bishops.size());
		assertTrue(bishops.stream().map(ChessPiece::getPosition)
				.toList().containsAll(List.of(new BoardPosition(2,0), new BoardPosition(5, 0))));
	}

	@Test
	public void initiate_blackPiecesAreValid() {
		List<String> users = new ArrayList<>();
		users.add("user1");
		users.add("user2");
		Mockito.when(gameRepository.save(any())).then(AdditionalAnswers.returnsFirstArg());
		ChessGame initiatedGame = gameService.initiate(users);

		List<ChessPiece> blackPawns = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.PAWN)
				.filter(piece -> piece.getColor().equals(PieceColor.BLACK))
				.collect(Collectors.toList());

		assertEquals(8, blackPawns.size());
		blackPawns.forEach(piece -> assertEquals(6, piece.getPosition().getY()));
		List<Integer> blackPawnsPlace = blackPawns.stream().map(ChessPiece::getPosition)
				.map(BoardPosition::getX).collect(Collectors.toList());
		assertTrue(blackPawnsPlace.containsAll(List.of(0, 1, 2, 3, 4, 5, 6, 7)));

		ChessPiece blackKing = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.KING)
				.filter(piece -> piece.getColor().equals(PieceColor.BLACK)).findFirst().get();
		assertEquals(new BoardPosition(4, 7), blackKing.getPosition());

		ChessPiece blackQueen = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.QUEEN)
				.filter(piece -> piece.getColor().equals(PieceColor.BLACK)).findFirst().get();
		assertEquals(new BoardPosition(3, 7), blackQueen.getPosition());

		List<ChessPiece> rooks = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.ROOK)
				.filter(piece -> piece.getColor().equals(PieceColor.BLACK))
				.collect(Collectors.toList());
		assertEquals(2, rooks.size());
		assertTrue(rooks.stream().map(ChessPiece::getPosition)
				.toList().containsAll(List.of(new BoardPosition(0,7), new BoardPosition(7,7))));

		List<ChessPiece> knights = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.KNIGHT)
				.filter(piece -> piece.getColor().equals(PieceColor.BLACK))
				.collect(Collectors.toList());
		assertEquals(2, knights.size());
		assertTrue(knights.stream().map(ChessPiece::getPosition)
				.toList().containsAll(List.of(new BoardPosition(1,7), new BoardPosition(6, 7))));

		List<ChessPiece> bishops = initiatedGame.getStates().get(0).getPieces()
				.stream().filter(piece -> piece.getType() == PieceType.BISHOP)
				.filter(piece -> piece.getColor().equals(PieceColor.BLACK))
				.collect(Collectors.toList());
		assertEquals(2, bishops.size());
		assertTrue(bishops.stream().map(ChessPiece::getPosition)
				.toList().containsAll(List.of(new BoardPosition(2,7), new BoardPosition(5, 7))));
	}

	@TestConfiguration
	@ComponentScan(basePackageClasses = GameService.class)
	public static class Configuration{

	}
}
