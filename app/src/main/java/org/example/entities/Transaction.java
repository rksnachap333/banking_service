package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.TransactionType;
import org.example.utils.TransactionTypeConverter;

import java.time.Instant;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double transactionAmount;

    private double amount;

    private String source;

    private Instant transactionDate;

    @Convert(converter = TransactionTypeConverter.class)
    private TransactionType type;

    @ManyToOne
    @JsonIgnore // Prevent infinite loop
    @JoinColumn(name = "account_id")
    private Account account;

}
