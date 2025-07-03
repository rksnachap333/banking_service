package org.example.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(length = 12, nullable = false, unique = true)
//    @Size(min = 12, max = 12, message = "Account number must be exactly 12 digits")
//    @Pattern(regexp = "\\d{12}", message = "Account number must be numeric and 12 digits")
    private String accountNumber;

    private String accountHolderName;

    private double balance;


    @OneToOne
    @JsonIgnore // Prevent infinite loop
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private User user;

    @OneToMany(mappedBy = "account")
    @JsonIgnore // Prevent infinite loop
    private List<Transaction> transactions;
}
