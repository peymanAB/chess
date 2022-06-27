package com.sensysgatso.chess.api.web.spec;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class NextMoveRequest {
	
	@NotBlank(message = "pieceId is mandatory")
	private String pieceId;
	
	@Valid
	private BoardPositionDto destination;
}
