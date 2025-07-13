package org.example.services;

import org.example.entities.Account;
import org.example.entities.User;
import org.example.enums.AccountType;
import org.example.repository.AccountRepository;
import org.example.repository.UserRepository;
import org.example.request.UserDTO;
import org.example.response.AccountDetailResponseDTO;
import org.example.utils.AccountHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserRepository userRepository;

    public Account createAccount(UserDTO userDTO) {
        Optional<User> user = userRepository.findByUsername(userDTO.getUsername());
        Account savedAccount =  createAccount(Account.builder()
                .user(user.get())
                .accountHolderName(userDTO.accountHolderName)
                .balance(0.0)
                .build());

        savedAccount.setAccountNumber(AccountHelper.generateAccountNumber(AccountType.SAVING,savedAccount.getId()));
        return accountRepository.save(savedAccount);
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> getAccount(Long id) {
        return accountRepository.findById(id);
    }

    public Boolean isAccountExists(String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        return account.isPresent();
    }

    public AccountDetailResponseDTO getAccountDetail(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()) {
            System.out.println("AccountDetail: Inside user present");
            Optional<Account>account= accountRepository.findByUserId(user.get().getId());
            Account accountDetail = account.get();
            AccountDetailResponseDTO accountDetailResponseDTO = AccountDetailResponseDTO.builder()
                    .accountHolderName(accountDetail.getAccountHolderName())
                    .accountNumber(accountDetail.getAccountNumber())
                    .address("Lig-23, Sarita Vihar , New Delhi- 110076")
                    .userCreatedOn(user.get().getUserCreatedOn())
                    .balance(accountDetail.getBalance())
                    .build();
            return accountDetailResponseDTO;
        } else {
            System.out.println("AccountDetail: User not found ==");
            return null;
        }
    }
}
