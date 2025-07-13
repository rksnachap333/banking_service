package org.example.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AccountDetailResponseDTO {
    private String accountNumber;
    private String accountHolderName;
    private String address;
    private double balance;
    private Instant userCreatedOn;
}
