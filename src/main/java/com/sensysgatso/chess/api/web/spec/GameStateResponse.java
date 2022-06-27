package com.sensysgatso.chess.api.web.spec;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class GameStateResponse extends GeneralResponse{

	public GameStateResponse() {
		setStatus(Result.SUCCESS);
	}
	
	public GameStateResponse(Result result) {
		setStatus(result);
	}
	
	private String gameId;
	
	private boolean finished;
	
	private String winner;
	
	private String lastMovedUser;
	
	private List<PieceDTO> pieces;
}
