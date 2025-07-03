package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@AllArgsConstructor
@Controller
@RequestMapping("/account/v1/")
public class AccountController {

    @Autowired
    AccountService accountService;

}
