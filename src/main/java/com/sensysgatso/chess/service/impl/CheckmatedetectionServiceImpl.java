package com.sensysgatso.chess.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sensysgatso.chess.common.ChessUtils;
import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.ChessPiece.BoardPosition;
import com.sensysgatso.chess.data.GameState;
import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.service.CheckmateDetectionService;
import com.sensysgatso.chess.service.PieceMovementStrategy;
import com.sensysgatso.chess.service.model.MoveRequest;
import com.sensysgatso.chess.service.model.NextMoveModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CheckmatedetectionServiceImpl implements CheckmateDetectionService{

	private final Map<PieceType, PieceMovementStrategy> strategies;

	public CheckmatedetectionServiceImpl(final List<PieceMovementStrategy> strategies) {
		this.strategies = strategies.stream()
				.collect(Collectors.toMap(strategy 
						-> strategy.supportedType(), strategy -> strategy));
	}

	@Override
	public boolean isCheckmate(ChessPiece king, GameState state) {
		if(!isKingCheked(king, state)) {
			return false;
		}
		return canKingBeSaved(king, state) ? false : true;
	}

	@Override
	public boolean isKingCheked(ChessPiece king, GameState state) {
		log.debug("loading all of the king's opponents");
		List<ChessPiece> opponentPieces = state.getPieces().stream().filter(piece -> !piece.isRemoved())
				.filter(piece -> !piece.getColor().equals(king.getColor()))
				.collect(Collectors.toList());
		for(ChessPiece piece : opponentPieces) {
			log.debug("checking if the king = {} is reachable by piece = {}", king.getColor(), piece.getPieceId());
			NextMoveModel model = new NextMoveModel();
			model.setRequest(MoveRequest.builder()
					.destination(king.getPosition())
					.pieceId(piece.getPieceId())
					.build());
			model.setLastState(state);
			model.setSelectedPiece(piece);
			boolean isKingReachable = strategies.get(piece.getType()).isMovementPossible(model);
			if(isKingReachable) {
				return true;
			}
		}
		return false;
	}

	private boolean canKingBeSaved(ChessPiece king, GameState state) {
		log.debug("going to check if the king could be saved from the check situation");
		List<ChessPiece> playerPieces = state.getPieces()
				.stream()
				.filter(piece -> !piece.isRemoved())
				.filter(piece -> piece.getColor().equals(king.getColor()))
				.collect(Collectors.toList());
		
		// TODO : move it to a CompletableFuture for performance improvement
		
		for(int x = 0 ; x < 8 ; x ++) {
			for( int y = 0 ; y < 8 ; y ++) {
				for(ChessPiece piece : playerPieces) {
					if(piece.getPosition().equals(new BoardPosition(x, y)))
						continue;
					NextMoveModel model = new NextMoveModel();
					model.setRequest(MoveRequest.builder()
							.destination(new BoardPosition(x, y))
							.pieceId(piece.getPieceId())
							.build());
					model.setLastState(state);
					model.setSelectedPiece(piece);
					boolean isMovePossible = strategies.get(piece.getType()).isMovementPossible(model);
					if(isMovePossible) {
						GameState hypotheticalState = ChessUtils.generateState(model);
						boolean isKingCheked;
						if(piece.getType().equals(PieceType.KING)) {
							ChessPiece updatedKing = hypotheticalState.getPieces().stream()
									.filter(p -> p.getPieceId().equals(king.getPieceId())).findFirst()
									.get();
							isKingCheked = isKingCheked(updatedKing, hypotheticalState);
						}else {
							isKingCheked = isKingCheked(king, hypotheticalState);						}
						
						if(!isKingCheked) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
