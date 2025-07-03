package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.entities.Token;
import org.example.request.AuthRequestDTO;
import org.example.request.RefreshTokenDTO;
import org.example.response.JwtResponseDTO;
import org.example.services.JwtService;
import org.example.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth/v1/")
public class TokenController
{
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("login")
    public ResponseEntity authenticationAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(),authRequestDTO.getPassword()));
            if(authentication.isAuthenticated()) {
                Token refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
                return new ResponseEntity<>(JwtResponseDTO.builder()
                        .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                        .token(refreshToken.getToken())
                        .build(), HttpStatus.OK
                );
            } else{
                return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace(); // <-- log the real issue here
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }

    }

    @PostMapping("refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO)
    {
        return refreshTokenService.findByToken(refreshTokenDTO.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(Token::getUser)
                .map(userInfo -> {
                            String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                            return JwtResponseDTO.builder()
                                    .accessToken(accessToken)
                                    .token(refreshTokenDTO.getRefreshToken()).build();
                        }
                ).orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));
    }

}
