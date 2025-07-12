package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.entities.Token;
import org.example.entities.User;
import org.example.repository.UserRepository;
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

import java.util.Optional;

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

    @Autowired
    private UserRepository userRepository;

    @PostMapping("login")
    public ResponseEntity authenticationAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        try{
            Optional<User> user = userRepository.findByUsername(authRequestDTO.getUsername());
            if(user.isPresent()) {
                System.out.println("User Found Now will check Password !!");
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(),authRequestDTO.getPassword()));
                if(authentication.isAuthenticated()) {
                    Token refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
                    return new ResponseEntity<>(JwtResponseDTO.builder()
                            .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                            .token(refreshToken.getToken())
                            .build(), HttpStatus.OK
                    );
                } else{
                    System.out.println("User Found  But Password is wrong!!");
                    return new ResponseEntity<>("Incorrect password", HttpStatus.UNAUTHORIZED);
                }
            } else{
                System.out.println("User Not Found !!");
                return new ResponseEntity<>("Userid not found !!", HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            System.out.println("User Finding meat with exception !!");
            e.printStackTrace(); // <-- log the real issue here
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
