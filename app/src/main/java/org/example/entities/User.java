package org.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private Instant userCreatedOn;

    @OneToOne
    @JsonIgnore // Prevent infinite loop
    @JoinColumn(name = "user")
    private Account account;

    @OneToOne(mappedBy = "user")
    @JsonIgnore // Prevent infinite loop
    private Token token;

}
