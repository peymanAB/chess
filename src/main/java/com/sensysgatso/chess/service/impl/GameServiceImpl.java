package com.sensysgatso.chess.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import com.sensysgatso.chess.common.ChessUtils;
import com.sensysgatso.chess.data.ChessGame;
import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.GameRepository;
import com.sensysgatso.chess.data.GameState;
import com.sensysgatso.chess.data.PieceColor;
import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.exception.GameNotFoundException;
import com.sensysgatso.chess.service.CheckmateDetectionService;
import com.sensysgatso.chess.service.GameService;
import com.sensysgatso.chess.service.MovementValidator;
import com.sensysgatso.chess.service.model.NextMoveModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GameServiceImpl implements GameService{

	private final GameRepository gameRepository;

	private final List<MovementValidator> validators;

	private final CheckmateDetectionService checkmateDetectionService;

	public GameServiceImpl(final GameRepository gameRepository,final List<MovementValidator> validators,
			final CheckmateDetectionService checkmateDetectionService) {
		this.gameRepository = gameRepository;
		this.validators = validators;
		this.checkmateDetectionService = checkmateDetectionService;
		Collections.sort(this.validators, Comparator.comparingInt(MovementValidator::order));
	}

	@Override
	public ChessGame initiate(List<String> participants) {
		log.debug("going to initiate game with participants = {}", participants);
		ChessGame game = new ChessGame();
		game.setCreationDate(System.currentTimeMillis());
		game.setFinished(false);
		game.setUid(UUID.randomUUID().toString());

		String whitePlayer = participants.get(RandomUtils.nextInt(0, 2));
		participants.remove(whitePlayer);
		String blackPlayer = participants.get(0);
		
		log.debug("the white playe is {} and the black player is {}", whitePlayer, blackPlayer);

		GameState initialState = new GameState();
		initialState.setMoveNumber(0);
		initialState.setTimestamp(System.currentTimeMillis());

		List<ChessPiece> pieces = ChessUtils.getGameInitiationTemplate();
		pieces.forEach(piece -> {
			String pieceOwner = piece.getColor().equals(PieceColor.WHITE) ? whitePlayer : blackPlayer;
			piece.setOwner(pieceOwner);
		});
		initialState.setPieces(pieces);
		game.setStates(List.of(initialState));
		return gameRepository.save(game);
	}

	@Override
	public ChessGame move(final NextMoveModel nextMoveModel) {
		String gameId = nextMoveModel.getRequest().getGameId();
		log.debug("going to load game with id = {}", gameId);
		ChessGame currentGame = gameRepository.findByUid(gameId)
				.filter(game -> !game.isFinished())
				.orElseThrow(() -> new GameNotFoundException("there is no game with id = " + gameId));
		nextMoveModel.setGame(currentGame);
		nextMoveModel.setLastState(getLastState(currentGame));
		nextMoveModel.setSelectedPiece(getSelectedPiece(nextMoveModel.getLastState(), nextMoveModel));

		log.debug("going to exceute validation chain on the move request model = {}", nextMoveModel);
		validators.stream().forEach(validator -> validator.validateMove(nextMoveModel));
		log.debug("the movement request is valid. going to move the piece = {}",
				nextMoveModel.getRequest().getPieceId());
		moveThePiece(nextMoveModel);
		log.debug("going to check if the King has fallen and the game is finished.");
		checkIfTheGameIsFinished(nextMoveModel);
		return gameRepository.save(currentGame);
	}

	private void checkIfTheGameIsFinished(NextMoveModel model) {
		ChessPiece endangeredKing = model.getLastState()
				.getPieces().stream()
				.filter(piece -> piece.getType() == PieceType.KING)
				.filter(piece -> !piece.getColor().equals(model.getSelectedPiece().getColor()))
				.findFirst().get();
		if(checkmateDetectionService.isCheckmate(endangeredKing, model.getLastState())) {
			log.debug("the king has fallen. going to set the game as finished with user = {} as winner",
					model.getRequest().getUserId());
			endangeredKing.setRemoved(true);
			model.getGame().setFinished(true);
			model.getGame().setFinishTime(System.currentTimeMillis());
			model.getGame().setWinner(model.getRequest().getUserId());
		}
	}

	private void moveThePiece(final NextMoveModel move) {
		GameState newState = ChessUtils.generateState(move);
		move.getGame().getStates().add(newState);
		move.setLastState(newState);
	}

	private GameState getLastState(ChessGame game) {
		return CollectionUtils.emptyIfNull(game.getStates()).stream()
				.sorted(Comparator.comparingInt(GameState::getMoveNumber).reversed())
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("the game has no states"));
	}

	private ChessPiece getSelectedPiece(GameState state, NextMoveModel move) {
		return state.getPieces().stream()
				.filter(piece -> piece.getPieceId().equals(move.getRequest().getPieceId()))
				.findFirst()
				.get();
	}
}
