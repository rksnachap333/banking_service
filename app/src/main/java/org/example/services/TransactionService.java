package org.example.services;

import org.example.entities.Account;
import org.example.entities.Transaction;
import org.example.entities.User;
import org.example.enums.TransactionType;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.example.request.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserRepository userRepository;

    public Transaction addTransaction(Transaction transaction)
    {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactionOfUserId(Long userId)
    {
        Optional<Account> account = accountRepository.findById(userId);
        if(!account.isPresent())
            return new ArrayList<>();
        return account.get().getTransactions();
    }

    public Transaction deposit(TransactionDTO transactionDTO) {
        Account account = accountRepository.findByAccountNumber(transactionDTO.getAccountNumber()).orElseThrow(()-> new RuntimeException("Account not found!!"));
        account.setBalance(account.getBalance() + transactionDTO.getAmount());
        accountRepository.save(account);
        Transaction transaction = createTransaction(transactionDTO, account, TransactionType.DEPOSIT);
        return transactionRepository.save(transaction);
    }

    public Transaction withdraw(TransactionDTO transactionDTO)
    {
        Account account = accountRepository.findByAccountNumber(transactionDTO.getAccountNumber()).orElseThrow(()-> new RuntimeException("Account not found!!"));
        if(account.getBalance() < transactionDTO.getAmount()){
            throw new RuntimeException("Insufficient Funds !!");
        }
        account.setBalance(account.getBalance() - transactionDTO.getAmount());
        accountRepository.save(account);
        Transaction transaction = createTransaction(transactionDTO, account, TransactionType.WITHDRAWAL);
        return transactionRepository.save(transaction);
    }

    public Transaction transfer(TransactionDTO transactionDTO)
    {
        Account recpientAcc = accountRepository.findByAccountNumber(transactionDTO.getToAccountNumber()).orElseThrow(()-> new RuntimeException("Recpient Account not found"));
        Account account = accountRepository.findByAccountNumber(transactionDTO.getAccountNumber()).orElseThrow(()-> new RuntimeException("Account not found"));
        if(account.getBalance() < transactionDTO.getAmount()){
            throw new RuntimeException("Insufficient Funds !!");
        }
        recpientAcc.setBalance(recpientAcc.getBalance() + transactionDTO.getAmount());
        account.setBalance(account.getBalance() - transactionDTO.getAmount());
        accountRepository.save(recpientAcc);
        accountRepository.save(account);
        Transaction transaction = createTransaction(transactionDTO, recpientAcc, TransactionType.DEPOSIT);
        transactionRepository.save(transaction);

        Transaction transaction1 = createTransaction(transactionDTO, account, TransactionType.TRANSFER);
        return transactionRepository.save(transaction1);

    }

    private Transaction createTransaction(TransactionDTO transactionDTO, Account account, TransactionType transactionType) {
        Transaction transaction = Transaction.builder()
                .transactionAmount(transactionDTO.getAmount())
                .transactionDate(Instant.now())
                .type(transactionType)
                .source(transactionDTO.getRemark())
                .account(account)
                .amount(account.getBalance())
                .build();
        return transaction;
    }

    public List<Transaction> transactionHistory(TransactionDTO transactionDTO) {
        Account account = accountRepository.findByAccountNumber(transactionDTO.getAccountNumber()).orElseThrow(() -> new RuntimeException("Account Not Found !!"));
        return account.getTransactions();
    }

    public List<Transaction> getTransactionSummary(String username, int limit) {
        System.out.println("TransactionSummary : Inside getTransactionSummary 11");
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            System.out.println("TransactionSummary : User is present");
            Optional<Account> accountOpt = accountRepository.findByUserId(user.get().getId());
            System.out.println("TransactionSummary : User is present 11");
            Account accountDetail = accountOpt.get();
            System.out.println("TransactionSummary : User is present 22");
            Account account = accountRepository.findByAccountNumber(accountDetail.getAccountNumber()).orElseThrow(() -> new RuntimeException("Account Not Found !!"));
            return account.getTransactions().stream()
                    .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed()) // sort by latest first
                    .limit(10)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public List<Transaction> getTransactionSummary(String username, Instant from, Instant to) {
        System.out.println("TransactionSummary : Inside getTransactionSummary 221");
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            System.out.println("TransactionSummary : User is present");
            Optional<Account> accountOpt = accountRepository.findByUserId(user.get().getId());
            System.out.println("TransactionSummary : User is present 2211");
            Account accountDetail = accountOpt.get();
            System.out.println("TransactionSummary : User is present 2222");
            Account account = accountRepository.findByAccountNumber(accountDetail.getAccountNumber()).orElseThrow(() -> new RuntimeException("Account Not Found !!"));
            return account.getTransactions().stream()
                    .filter(t -> {
                        Instant date = t.getTransactionDate();
                        return (date != null && !date.isBefore(from) && !date.isAfter(to));
                    })
                    .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed()) // latest first
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
