package com.sensysgatso.chess.data;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<ChessGame, String>{
	
	Optional<ChessGame> findByUid(String gameId);
}
