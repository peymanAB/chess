package com.sensysgatso.chess.api.web.spec;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class GameInitiationRequest {

	private List<String> participants;
}
