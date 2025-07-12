package org.example.services;

import org.example.entities.Token;
import org.example.entities.User;
import org.example.repository.TokenRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    public Token createRefreshToken(String username) {
        Optional<User> userExtracted = userRepository.findByUsername(username);
        if (userExtracted.isPresent()) {
            Optional<Token> token = tokenRepository.findByUserUserId(userExtracted.get().getId());
            if(token.isPresent()){
                //updating the new token to existing row
                Token tokenToReplace = token.get();
                tokenToReplace.setToken(UUID.randomUUID().toString());
                tokenToReplace.setExpiryDate(Instant.now().plusMillis(1000 * 60 * 60 * 24 * 15));
                return tokenRepository.save(tokenToReplace); // update existing token
            } else{
                //adding new token in the db
                Token refreshToken = Token.builder()
                        .user(userExtracted.get())
                        .token(UUID.randomUUID().toString())
                        .expiryDate(Instant.now().plusMillis(1000 * 60 * 60 * 24 * 15)) // 15 days
                        .build();
                return tokenRepository.save(refreshToken);
            }

        } else {
            throw new UnsupportedOperationException("User not found !!");
        }
    }
    public Token verifyExpiration(Token token) {
        if(token.getExpiryDate().compareTo(Instant.now()) < 0) {
            tokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + "Refresh Token is Expired, Please make a new login.. !");
        }
        return token;
    }

    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

}
