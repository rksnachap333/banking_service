package org.example.repository;

import org.example.entities.Token;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, Integer> {
    Optional<Token> findByToken(String token);
    Optional<Token> findTokenByUserId(Long userId);
}
