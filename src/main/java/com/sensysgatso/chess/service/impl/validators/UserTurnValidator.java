package com.sensysgatso.chess.service.impl.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.sensysgatso.chess.api.web.spec.Result;
import com.sensysgatso.chess.data.PieceColor;
import com.sensysgatso.chess.exception.InvalidMoveException;
import com.sensysgatso.chess.service.MovementValidator;
import com.sensysgatso.chess.service.model.NextMoveModel;

@Service
public class UserTurnValidator implements MovementValidator{

	@Override
	public void validateMove(NextMoveModel nextMoveModel) {
		if(nextMoveModel.getLastState().getMoveNumber() == 0 &&
				StringUtils.isBlank(nextMoveModel.getLastState().getUserId())) {
			if(nextMoveModel.getSelectedPiece().getColor() != PieceColor.WHITE) {
				throw new InvalidMoveException("The first move should be made by the white player",
						Result.OUT_OF_TURN_MOVEMENT);
			}
		}
		if(nextMoveModel.getRequest().getUserId().equals(nextMoveModel.getLastState().getUserId())) {
			throw new InvalidMoveException("User cannot move the piece cause it's not his/her turn",
					Result.OUT_OF_TURN_MOVEMENT);
		}
	}
	
	@Override
	public int order() {
		return 1;
	}
}
