package com.sensysgatso.chess.transformer;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.sensysgatso.chess.api.web.spec.BoardPositionDto;
import com.sensysgatso.chess.api.web.spec.GameStateResponse;
import com.sensysgatso.chess.api.web.spec.NextMoveRequest;
import com.sensysgatso.chess.api.web.spec.PieceDTO;
import com.sensysgatso.chess.data.ChessGame;
import com.sensysgatso.chess.data.ChessPiece;
import com.sensysgatso.chess.data.ChessPiece.BoardPosition;
import com.sensysgatso.chess.data.GameState;
import com.sensysgatso.chess.service.model.NextMoveModel;

@Mapper(componentModel = "spring")
public interface GameMapper {

	@Mapping(target = "request.gameId", source = "gameId")
	@Mapping(target = "request.userId", source = "userId")
	@Mapping(target = "request.pieceId", source = "request.pieceId")
	@Mapping(target = "request.destination", source = "request.destination")
	NextMoveModel toNextMoveModel(NextMoveRequest request, String userId, String gameId);

	@Mapping(target = "gameId", source = "uid")
	@Mapping(target = "lastMovedUser", source = "game.states", qualifiedByName = "getLastMovedUser")
	@Mapping(target = "pieces", source = "game.states", qualifiedByName = "toPieces")
	GameStateResponse toGameStateResponse(ChessGame game);
	
	PieceDTO toPieceDTO(ChessPiece piece);
	
	BoardPositionDto toBoardPositionDto(BoardPosition position);
	
	@InheritInverseConfiguration
	BoardPosition toBoardPosition(BoardPositionDto dto);
	
	@Named("getLastMovedUser")
	default String getLastMovedUser(List<GameState> states) {
		return states.get(states.size() - 1).getUserId();
	}
	
	@Named("toPieces")
	default List<PieceDTO> toPieces(List<GameState> states){
		return states.get(states.size() - 1).getPieces()
				.stream()
				.map(this::toPieceDTO)
				.collect(Collectors.toList());
	}
}
