package org.example.controllers;


import lombok.AllArgsConstructor;
import org.example.entities.Transaction;
//import org.example.kafka.producer.KafkaProducerService;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.example.request.TransactionDTO;
import org.example.response.TransactionHistoryDTO;
import org.example.response.TransactionResponseDTO;
import org.example.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/transaction/v1/")
public class TransactionController
{

    @Autowired
    TransactionService transactionService;

//    @Autowired
//    KafkaProducerService kafkaProducerService;

    @PostMapping("deposit")
    public ResponseEntity deposit(@RequestBody TransactionDTO transactionDTO)
    {
        try {
            Transaction transaction = transactionService.deposit(transactionDTO);
//            try {
//                kafkaProducerService.sendMessage("Deposit Done !!");
//            } catch (Exception kafkaEx) {
//                // ✅ Kafka error should not affect user response
//                System.err.println("Kafka send failed: " + kafkaEx.getMessage());
//                // You can log this with a proper logger like SLF4J as well
//            }
            return new ResponseEntity<>(TransactionResponseDTO.builder()
                    .transactionId(transaction.getId())
                    .remarks("Transaction Success !!")
                    .build(), HttpStatus.OK
            );

        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

    @PostMapping("withdraw")
    public ResponseEntity withdraw(@RequestBody TransactionDTO transactionDTO)
    {
        try {
            Transaction transaction = transactionService.withdraw(transactionDTO);
            return new ResponseEntity<>(TransactionResponseDTO.builder()
                    .transactionId(transaction.getId())
                    .remarks("Transaction Success !!")
                    .build(), HttpStatus.OK
            );

        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PostMapping("transfer")
    public ResponseEntity transfer(@RequestBody TransactionDTO transactionDTO)
    {
        try {
            Transaction transaction = transactionService.transfer(transactionDTO);
            return new ResponseEntity<>(TransactionResponseDTO.builder()
                    .transactionId(transaction.getId())
                    .remarks("Transaction Success !!")
                    .build(), HttpStatus.OK
            );

        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @PostMapping("transactions")
    public ResponseEntity transactionHistory(@RequestBody TransactionDTO transactionDTO)
    {
        try {
            List<Transaction> transactions = transactionService.transactionHistory(transactionDTO);
            return new ResponseEntity<>(TransactionHistoryDTO.builder()
                    .transactions(transactions)
                    .build(), HttpStatus.OK
            );

        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
