package com.sensysgatso.chess.service.impl.validators;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sensysgatso.chess.api.web.spec.Result;
import com.sensysgatso.chess.data.PieceType;
import com.sensysgatso.chess.exception.InvalidMoveException;
import com.sensysgatso.chess.service.MovementValidator;
import com.sensysgatso.chess.service.PieceMovementStrategy;
import com.sensysgatso.chess.service.model.NextMoveModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DestinationValidator implements MovementValidator{

	private final Map<PieceType, PieceMovementStrategy> strategies;
	
	public DestinationValidator(final List<PieceMovementStrategy> strategies) {
		this.strategies = strategies.stream()
				.collect(Collectors.toMap(strategy 
						-> strategy.supportedType(), strategy -> strategy));
	}
	
	@Override
	public void validateMove(NextMoveModel nextMoveModel) {
		log.debug("cheching movement possbility for piece={}-{}",
				nextMoveModel.getSelectedPiece().getType(),
				nextMoveModel.getSelectedPiece().getPieceId());
		boolean isMovementPossible = strategies.get(nextMoveModel.getSelectedPiece()
				.getType()).isMovementPossible(nextMoveModel);
		if(!isMovementPossible) {
			throw new InvalidMoveException("the requested movement is not possible for piece",
					Result.INVALID_PIECE_MOVEMENT);
		}
		
	}
	
	@Override
	public int order() {
		return 5;
	}
}
