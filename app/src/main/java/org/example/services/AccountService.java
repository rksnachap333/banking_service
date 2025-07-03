package org.example.services;

import org.example.entities.Account;
import org.example.entities.User;
import org.example.enums.AccountType;
import org.example.repository.AccountRepository;
import org.example.repository.UserRepository;
import org.example.request.UserDTO;
import org.example.utils.AccountHelper;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Account deposit(Long id, double amount) {
        Account account = getAccount(id).orElseThrow(() -> new RuntimeException("Account Not Found !!"));
        account.setBalance(account.getBalance()+amount);
        return accountRepository.save(account);
    }

    public Account withdraw(Long id, double amount) {
        Account account = getAccount(id).orElseThrow(() -> new RuntimeException("Account Not Found !!"));
        if(account.getBalance() < amount){
            throw new RuntimeException("Insufficient Funds !!");
        }
        account.setBalance(account.getBalance() - amount);
        return accountRepository.save(account);
    }

    public Boolean isAccountExists(String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        return account.isPresent();
    }
}
