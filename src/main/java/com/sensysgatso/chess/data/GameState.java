package com.sensysgatso.chess.data;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class GameState implements Serializable{

	private static final long serialVersionUID = -7178908083697090688L;

	private Long timestamp;
	
	private Integer moveNumber;
	
	private String userId;
	
	private List<ChessPiece> pieces;
}
