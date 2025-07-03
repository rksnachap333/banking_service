package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.entities.Account;
import org.example.entities.Token;
import org.example.entities.User;
import org.example.repository.UserRepository;
import org.example.request.UserDTO;
import org.example.response.JwtResponseDTO;
import org.example.response.SignupResponseDTO;
import org.example.services.AccountService;
import org.example.services.JwtService;
import org.example.services.RefreshTokenService;
import org.example.services.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth/v1/")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    AccountService accountService;

    @PostMapping("signup")
    public ResponseEntity SignUp (@RequestBody UserDTO userDTO)
    {
        try{
            Boolean isAlreadySignUp = userDetailService.signupUser(userDTO);
            if(Boolean.TRUE.equals(isAlreadySignUp)) {
                return new ResponseEntity("User Already Exist !!", HttpStatus.BAD_REQUEST);
            }
//            Token refreshToken = refreshTokenService.createRefreshToken(userDTO.getUsername());
//            String jwtToken = jwtService.GenerateToken(userDTO.getUsername());
            Account account = accountService.createAccount(userDTO);
            return new ResponseEntity<>(SignupResponseDTO.builder()
                    .username(userDTO.getUsername())
                    .accountNumber(account.getAccountNumber())
                    .build(), HttpStatus.OK);

        } catch (Exception ex) {
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

}
