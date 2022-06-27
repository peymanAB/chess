package com.sensysgatso.chess.service.impl.validators;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sensysgatso.chess.api.web.spec.Result;
import com.sensysgatso.chess.data.ChessPiece.BoardPosition;
import com.sensysgatso.chess.exception.InvalidMoveException;
import com.sensysgatso.chess.service.MovementValidator;
import com.sensysgatso.chess.service.model.NextMoveModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PieceMobilityValidator implements MovementValidator{

	@Override
	public void validateMove(NextMoveModel nextMoveModel) {
		if(nextMoveModel.getSelectedPiece().isRemoved()) {
			log.debug("the piece ={} is already removed and cannot be moved",
					nextMoveModel.getSelectedPiece().getPieceId());
			throw new InvalidMoveException("removed piece cannot be moved", Result.PIECE_REMOVED);
		}

		if(!nextMoveModel.getSelectedPiece().getOwner().equals(nextMoveModel.getRequest().getUserId())) {
			log.error("user = {} does not own the piece = {}",
					nextMoveModel.getRequest().getUserId(), nextMoveModel.getRequest().getPieceId());
			throw new InvalidMoveException("user does not own the piece", Result.PIECE_OWNERSHIP);
		}
		BoardPosition target = nextMoveModel.getRequest().getDestination();

		boolean isTargetOutOfBound = List.of(target.getX(),
				target.getY()).stream().anyMatch(axis -> axis < 0 || axis > 7);

		if(isTargetOutOfBound) {
			throw new InvalidMoveException("the selected destination is out of bound",
					Result.OUT_OF_BOUND_DESTINATION);
		}

	}

	@Override
	public int order() {
		return 0;
	}
}
