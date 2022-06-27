package com.sensysgatso.chess.data;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChessPiece implements Serializable{

	private static final long serialVersionUID = -1124218879874408237L;

	private String pieceId;
	
	private PieceColor color;
	
	private String owner;
	
	private BoardPosition position;
	
	private PieceType type;
	
	private boolean removed;
	
	private boolean firstMove;

	@Setter
	@Getter
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BoardPosition implements Serializable{
		private static final long serialVersionUID = -1457076905717164983L;
		
		private int x;
		private int y;
	}
}
