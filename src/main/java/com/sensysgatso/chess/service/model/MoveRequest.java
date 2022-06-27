package com.sensysgatso.chess.service.model;

import com.sensysgatso.chess.data.ChessPiece.BoardPosition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveRequest {

	private String gameId;
	
	private String userId;
	
	private String pieceId;
	
	private BoardPosition destination;

}
