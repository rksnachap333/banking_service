package org.example.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO
{
    private double amount;
    private String accountNumber;
    private String toAccountNumber;
    private String remark;

}
