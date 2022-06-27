package com.sensysgatso.chess.api.web;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sensysgatso.chess.api.web.spec.GameInitiationRequest;
import com.sensysgatso.chess.api.web.spec.GameStateResponse;
import com.sensysgatso.chess.api.web.spec.GeneralResponse;
import com.sensysgatso.chess.api.web.spec.NextMoveRequest;
import com.sensysgatso.chess.api.web.spec.Result;
import com.sensysgatso.chess.data.ChessGame;
import com.sensysgatso.chess.service.GameService;
import com.sensysgatso.chess.transformer.GameMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameResource {
	
	private final GameMapper mapper;
	
	private final GameService gameService;
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<? extends GeneralResponse> initiateNewGame(@RequestBody final GameInitiationRequest request){
		log.debug("going to initiate a new chess game with request = {}", request);
		if(CollectionUtils.isEmpty(request.getParticipants()) || request.getParticipants().size() != 2){
			GeneralResponse response = new GeneralResponse();
			response.setMsg("Invalid number of participants");
			response.setStatus(Result.BAD_REQUEST);
			return ResponseEntity.badRequest().body(response);
		}
		ChessGame game = gameService.initiate(request.getParticipants());
		return ResponseEntity.ok(mapper.toGameStateResponse(game));
	}
	
	@PostMapping(path = "/{gameId}/play")
	public ResponseEntity<GameStateResponse> playNextMove(@PathVariable("gameId") final String gameId,
			@RequestHeader(name = "userId", required = false) final String userId,
			@RequestBody @Valid final NextMoveRequest request){
		log.debug("play request for game = {} by user = {} and details = {}", gameId, userId, request);
		ChessGame result = gameService.move(mapper.toNextMoveModel(request, userId, gameId));
		return ResponseEntity.ok(mapper.toGameStateResponse(result));
	}
}
