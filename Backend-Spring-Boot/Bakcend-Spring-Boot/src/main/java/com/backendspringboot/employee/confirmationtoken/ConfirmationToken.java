package com.backendspringboot.employee.confirmationtoken;

import com.backendspringboot.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConfirmationToken {
    //it will be generated automatically (will create a sequence)
    //A sequence is an object in Oracle that is used to generate a number sequence.
    //This can be useful when you need to create a unique number to act as a primary key
    @SequenceGenerator(name = "confirmation_token_sequence",
            sequenceName = "confirmation_token_sequence",
            //The amount to increment by when allocating sequence numbers from the sequence (by default it's 50, and we change it to 1)
            allocationSize = 1)
    //the id will be the primary key of our table
    @Id
    //how do we generate the value of the Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "confirmation_token_sequence")
    private Long id;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;

    //for every employee we have many token
    @ManyToOne
    @JoinColumn(nullable = false,name = "employee_id")
    private Employee employee;

    public ConfirmationToken(String token,
                             LocalDateTime createdAt,
                             LocalDateTime expiresAt,
                             Employee employee) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.employee = employee;
    }
}
