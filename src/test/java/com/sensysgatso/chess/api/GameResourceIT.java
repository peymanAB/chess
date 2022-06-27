package com.sensysgatso.chess.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.sensysgatso.chess.api.web.spec.BoardPositionDto;
import com.sensysgatso.chess.api.web.spec.GameInitiationRequest;
import com.sensysgatso.chess.api.web.spec.GameStateResponse;
import com.sensysgatso.chess.api.web.spec.NextMoveRequest;
import com.sensysgatso.chess.api.web.spec.PieceDTO;
import com.sensysgatso.chess.api.web.spec.Result;
import com.sensysgatso.chess.data.ChessGame;
import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.GameRepository;
import com.sensysgatso.chess.data.GameState;
import com.sensysgatso.chess.data.PieceColor;
import com.sensysgatso.chess.data.PieceType;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class GameResourceIT {

	private TestRestTemplate restTemplate = new TestRestTemplate();

	@Autowired
	private GameRepository gameRepository;

	@LocalServerPort
	private int port;

	@AfterEach
	public void destroy() {
		this.gameRepository.deleteAll();
	}

	@Test
	public void initiateNewGame_validData_gameInitiated(){
		GameInitiationRequest gameRequest = new GameInitiationRequest();
		gameRequest.setParticipants(List.of("user1", "user2"));
		HttpEntity<GameInitiationRequest> request = new HttpEntity<>(gameRequest);
		ResponseEntity<GameStateResponse> response = this.restTemplate.exchange(createUrl("/games"),
				HttpMethod.POST, request, GameStateResponse.class);
		assertTrue(response.getStatusCode().is2xxSuccessful());

		ChessGame game = gameRepository.findAll().get(0);
		assertNotNull(game.getUid());
		assertNotNull(game.getCreationDate());
		assertEquals(1, game.getStates().size());
		List<GameState> states = game.getStates();
		assertEquals(0, states.get(0).getMoveNumber());
		assertEquals(32, states.get(0).getPieces().size());

		GameStateResponse responseBody = response.getBody();
		assertEquals(game.getUid(), responseBody.getGameId());
		assertEquals(Result.SUCCESS, responseBody.getStatus());
		assertEquals(32, responseBody.getPieces().size());

		PieceDTO samplePiece = responseBody.getPieces().get(0);
		assertNotNull(samplePiece.getColor());
		assertNotNull(samplePiece.getOwner());
		assertNotNull(samplePiece.getPieceId());
		assertNotNull(samplePiece.getPosition());
		assertNotNull(samplePiece.getType());
	}

	@Test
	public void initiateNewGame_invalidNumberOfParticipants_return400() {
		GameInitiationRequest gameRequest = new GameInitiationRequest();
		gameRequest.setParticipants(List.of("user1"));
		HttpEntity<GameInitiationRequest> request = new HttpEntity<>(gameRequest);
		ResponseEntity<GameStateResponse> response = this.restTemplate.exchange(createUrl("/games"),
				HttpMethod.POST, request, GameStateResponse.class);
		assertEquals(400, response.getStatusCodeValue());
		assertEquals(Result.BAD_REQUEST, response.getBody().getStatus());
	}

	@Test
	public void playNextMove_emptyPieceId_return400() {
		NextMoveRequest nextMoveRequest = new NextMoveRequest();
		BoardPositionDto position = new BoardPositionDto();
		position.setX(5);
		position.setY(3); 
		nextMoveRequest.setDestination(position);
		HttpHeaders headers = new HttpHeaders();
		headers.set("userId", "user1");
		HttpEntity<NextMoveRequest> request = new HttpEntity<>(nextMoveRequest, headers);
		ResponseEntity<GameStateResponse> response = this.restTemplate.exchange(createUrl("/games/sampleId/play"),
				HttpMethod.POST, request, GameStateResponse.class);
		assertEquals(400, response.getStatusCodeValue());
		assertEquals(Result.BAD_REQUEST, response.getBody().getStatus());
		assertEquals("pieceId is mandatory", response.getBody().getMsg());
	}

	@Test
	public void playNextMove_emptyPositon_return400() {
		NextMoveRequest nextMoveRequest = new NextMoveRequest();
		BoardPositionDto position = new BoardPositionDto();
		position.setY(3); 
		nextMoveRequest.setDestination(position);
		nextMoveRequest.setPieceId("samplePieceId");
		HttpHeaders headers = new HttpHeaders();
		headers.set("userId", "user1");
		HttpEntity<NextMoveRequest> request = new HttpEntity<>(nextMoveRequest, headers);
		ResponseEntity<GameStateResponse> response = this.restTemplate.exchange(createUrl("/games/sampleId/play"),
				HttpMethod.POST, request, GameStateResponse.class);
		assertEquals(400, response.getStatusCodeValue());
		assertEquals(Result.BAD_REQUEST, response.getBody().getStatus());
		assertEquals("X axis is mandatory", response.getBody().getMsg());
	}

	@Test
	public void playNextMove_moveIsAllowed_return200() {
		GameInitiationRequest gameRequest = new GameInitiationRequest();
		gameRequest.setParticipants(List.of("user1", "user2"));
		HttpEntity<GameInitiationRequest> initRequest = new HttpEntity<>(gameRequest);
		ResponseEntity<GameStateResponse> initResponse = this.restTemplate.exchange(createUrl("/games"),
				HttpMethod.POST, initRequest, GameStateResponse.class);

		PieceDTO pawn = initResponse.getBody().getPieces().stream().filter(piece -> piece.getColor().equals(PieceColor.WHITE))
				.filter(piece -> piece.getType().equals(PieceType.PAWN))
				.filter(piece -> piece.getPosition().getX() == 2 && piece.getPosition().getY() == 1).findFirst().get();		


		NextMoveRequest nextMoveRequest = new NextMoveRequest();
		BoardPositionDto position = new BoardPositionDto();
		position.setY(3);
		position.setX(2);
		nextMoveRequest.setDestination(position);
		nextMoveRequest.setPieceId(pawn.getPieceId());
		HttpHeaders headers = new HttpHeaders();
		headers.set("userId", pawn.getOwner());
		HttpEntity<NextMoveRequest> request = new HttpEntity<>(nextMoveRequest, headers);
		ResponseEntity<GameStateResponse> response = this.restTemplate
				.exchange(createUrl("/games/" + initResponse.getBody().getGameId() + "/play"),
						HttpMethod.POST, request, GameStateResponse.class);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(Result.SUCCESS, response.getBody().getStatus());

		PieceDTO updatedPawn = response.getBody().getPieces().stream()
				.filter(piece -> piece.getPieceId().equals(pawn.getPieceId())).findFirst().get();

		assertEquals(2, updatedPawn.getPosition().getX());
		assertEquals(3, updatedPawn.getPosition().getY());

		ChessGame game = gameRepository.findByUid(initResponse.getBody().getGameId()).get();
		assertEquals(2, game.getStates().size());
		assertEquals(1, game.getStates().get(1).getMoveNumber());
		assertEquals(pawn.getOwner(), game.getStates().get(1).getUserId());
		ChessPiece piece = game.getStates().get(1).getPieces().stream()
				.filter(p -> p.getPieceId().equals(pawn.getPieceId())).findFirst().get();
		assertEquals(2, piece.getPosition().getX());
		assertEquals(3, piece.getPosition().getY());
	}
	
	private String createUrl(String url) {
		return new StringBuilder("http://localhost:").append(port).append("/api/")
				.append(url).toString();
	}
}
