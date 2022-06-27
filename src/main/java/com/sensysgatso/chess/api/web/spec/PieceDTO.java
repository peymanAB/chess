package com.sensysgatso.chess.api.web.spec;

import com.sensysgatso.chess.data.PieceColor;
import com.sensysgatso.chess.data.PieceType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PieceDTO {

	private String pieceId;
	
	private PieceColor color;
	
	private String owner;
	
	private BoardPositionDto position;
	
	private PieceType type;
	
	private boolean removed;
	
}
