package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.entities.Account;
import org.example.entities.User;
import org.example.repository.AccountRepository;
import org.example.repository.UserRepository;
import org.example.response.AccountDetailResponseDTO;
import org.example.response.JwtResponseDTO;
import org.example.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@AllArgsConstructor
@RestController
@RequestMapping("/account/v1/")
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("account_detail")
    public ResponseEntity getAccountDetail(@RequestParam("username") String username) {
        AccountDetailResponseDTO accountDetailResponseDTO = accountService.getAccountDetail(username);
        if(accountDetailResponseDTO != null) {
            return new ResponseEntity(accountDetailResponseDTO, HttpStatus.OK);
        }
        return new ResponseEntity("Account Not Found !!", HttpStatus.NOT_FOUND);

    }


}
