package com.sensysgatso.chess.api.web.spec;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GeneralResponse {

	private Result status;
	
	private String msg;
}
