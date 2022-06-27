package com.sensysgatso.chess.api;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.sensysgatso.chess.api.web.spec.BoardPositionDto;
import com.sensysgatso.chess.api.web.spec.GameInitiationRequest;
import com.sensysgatso.chess.api.web.spec.GameStateResponse;
import com.sensysgatso.chess.api.web.spec.NextMoveRequest;
import com.sensysgatso.chess.api.web.spec.PieceDTO;
import com.sensysgatso.chess.data.PieceColor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class GameSimulationIT {

	private RestTemplate restTemplate = new RestTemplate();
	
	@LocalServerPort
	private int port;

	@Test
	public void simulateGame() {
		List<Pair<BoardPositionDto, BoardPositionDto>> movements = List.of(
				Pair.of(new BoardPositionDto(4,1), new BoardPositionDto(4,3)),
				Pair.of(new BoardPositionDto(4,6), new BoardPositionDto(4,4)),
				Pair.of(new BoardPositionDto(6,0), new BoardPositionDto(5,2)),
				Pair.of(new BoardPositionDto(1,7), new BoardPositionDto(2,5)),
				Pair.of(new BoardPositionDto(5,0), new BoardPositionDto(2,3)),
				Pair.of(new BoardPositionDto(2,5), new BoardPositionDto(3,3)),
				Pair.of(new BoardPositionDto(5,2), new BoardPositionDto(4,4)),
				Pair.of(new BoardPositionDto(3,7), new BoardPositionDto(6,4)),
				Pair.of(new BoardPositionDto(4,4), new BoardPositionDto(5,6)),
				Pair.of(new BoardPositionDto(6,4), new BoardPositionDto(6,1)),
				Pair.of(new BoardPositionDto(7,0), new BoardPositionDto(5,0)),
				Pair.of(new BoardPositionDto(6,1), new BoardPositionDto(4,3)),
				Pair.of(new BoardPositionDto(2,3), new BoardPositionDto(4,1)),
				Pair.of(new BoardPositionDto(3,3), new BoardPositionDto(5,2))
				);

		GameStateResponse initiatedGame = initiateGame();
		String gameId = initiatedGame.getGameId();
		List<PieceDTO> lastState = initiatedGame.getPieces();
		
		String whitePlayer = initiatedGame.getPieces().stream()
				.filter(piece -> piece.getColor().equals(PieceColor.WHITE))
				.findAny().map(piece -> piece.getOwner()).get();
		
		String blackPlayer = "user1".equals(whitePlayer) ? "user2" : "user1";
		
		for(int i = 0 ; i < movements.size() ; i++) {
			log.info("going to execute move number = {}", i);
			BoardPositionDto source = movements.get(i).getLeft();
			BoardPositionDto destination = movements.get(i).getRight();
			String pieceId = lastState.stream().filter(dto -> dto.getPosition().equals(source))
					.filter(dto -> !dto.isRemoved())
					.findFirst().map(PieceDTO::getPieceId).get();
			String userId = (i % 2 != 0) ? blackPlayer : whitePlayer;
			GameStateResponse stateResponse = play(gameId, userId, pieceId, destination);
			if(stateResponse.isFinished()) {
				return;
			}
			lastState = stateResponse.getPieces();
		}
		
		throw new IllegalStateException("movemnets are done but the game is not finished");

	}
	
	private GameStateResponse play(String gameId, String userId, String pieceId, BoardPositionDto destination) {
		NextMoveRequest nextMoveRequest = new NextMoveRequest();
		nextMoveRequest.setDestination(destination);
		nextMoveRequest.setPieceId(pieceId);
		HttpHeaders headers = new HttpHeaders();
		headers.set("userId", userId);
		HttpEntity<NextMoveRequest> request = new HttpEntity<>(nextMoveRequest, headers);
		ResponseEntity<GameStateResponse> response = this.restTemplate
				.exchange(createUrl("/games/" + gameId+ "/play"),
						HttpMethod.POST, request, GameStateResponse.class);
		return response.getBody();
	}

	private GameStateResponse initiateGame() {
		GameInitiationRequest gameRequest = new GameInitiationRequest();
		gameRequest.setParticipants(List.of("user1", "user2"));
		HttpEntity<GameInitiationRequest> request = new HttpEntity<>(gameRequest);
		ResponseEntity<GameStateResponse> response = this.restTemplate.exchange(createUrl("/games"),
				HttpMethod.POST, request, GameStateResponse.class);
		return response.getBody();
	}
	
	private String createUrl(String url) {
		return new StringBuilder("http://localhost:").append(port).append("/api/")
				.append(url).toString();
	}
}
