package com.sensysgatso.chess.data;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document(collection = "chess_games")
public class ChessGame {

	@Id
	private String id;
	
	@Indexed(unique = true)
	private String uid;
	
	// TODO: use java 8+ date features
	private Long creationDate;
	
	@Version
	private Integer version;
	
	private boolean finished;
	
	private Long finishTime; 
	
	private String winner;
	
	private List<GameState> states;
}
