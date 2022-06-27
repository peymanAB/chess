package com.sensysgatso.chess.service.impl.validators;

import org.springframework.stereotype.Service;

import com.sensysgatso.chess.api.web.spec.Result;
import com.sensysgatso.chess.common.ChessUtils;
import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.GameState;
import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.exception.InvalidMoveException;
import com.sensysgatso.chess.service.CheckmateDetectionService;
import com.sensysgatso.chess.service.MovementValidator;
import com.sensysgatso.chess.service.model.NextMoveModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KingExposureValidator implements MovementValidator {

	private final CheckmateDetectionService detectionService;
	
	@Override
	public void validateMove(NextMoveModel nextMoveModel) {
		ChessPiece endangeredKing = nextMoveModel.getLastState()
				.getPieces().stream()
				.filter(piece -> piece.getType() == PieceType.KING)
				.filter(piece -> piece.getColor().equals(nextMoveModel.getSelectedPiece().getColor()))
				.findFirst().get();
		log.debug("going to check if the move could endanger the king");
		GameState newState = ChessUtils.generateState(nextMoveModel);
		
		if(detectionService.isKingCheked(endangeredKing, newState)) {
			throw new InvalidMoveException("Invalid move, king would be checked", Result.KING_EXPOSURE);
		}
	}
	
	@Override
	public int order() {
		return 10;
	}
}
