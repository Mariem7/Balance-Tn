package com.backendspringboot.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

//Serializable make transition from java class to database table easier
public class Employee implements Serializable {
    //it will be generated automatically (will create a sequence)
    //A sequence is an object in Oracle that is used to generate a number sequence.
    //This can be useful when you need to create a unique number to act as a primary key
    @SequenceGenerator(name = "employee_sequence",
            sequenceName = "employee_sequence",
            //The amount to increment by when allocating sequence numbers from the sequence (by default it's 50, and we change it to 1)
            allocationSize = 1)
    //the id will be the primary key of our table
    @Id
    //how do we generate the value of the Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_sequence")
    //for security reason we need to store different id
    //this one for the database
    @Column(nullable = false,updatable = false)
    private Long employeeIdDataBase;
    //this one for using it in the app
    //we will generate a random number to it
    private String employeeId;
    //the username which will be the email
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    //the function of the user {function in the society: accountant or CEO}
    private String function;
    //the Phone number
    private Long phoneNumber;
    //the National Identity Card Number
    private Long nicNumber;
    private String profileImageUrl;
    private String role; //ROLE_USER, ROLE_ADMIN
    private String[] authorities;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joindDate;
    private boolean isEnabled = false;
    private boolean isNotlocked;
}
