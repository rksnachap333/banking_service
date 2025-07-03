package org.example.services;

import org.example.entities.Account;
import org.example.entities.Transaction;
import org.example.enums.TransactionType;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.example.request.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    AccountRepository accountRepository;

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
}
