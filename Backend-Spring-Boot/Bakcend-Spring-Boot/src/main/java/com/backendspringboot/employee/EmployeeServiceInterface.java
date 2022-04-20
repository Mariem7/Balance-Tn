package com.backendspringboot.employee;

import com.backendspringboot.exception.EmployeeNotFoundException;
import com.backendspringboot.exception.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface EmployeeServiceInterface {

    //method that register an employee
    Employee register(String firstName,String lastName,String username,String password,String function,Long phoneNumber,Long nicNumber)
            throws UsernameExistException, EmployeeNotFoundException, MessagingException;

    //return all employees
    List<Employee> getEmployees();

    //return the employee based on his username
    Employee findEmployeeByUsername(String username);

    Employee addNewEmployee(String firstName, String lastName, String username, String function, Long phoneNumber, Long nicNumber,
                            String role, boolean isEnabled, boolean isNotlocked, MultipartFile profileImage) throws UsernameExistException, EmployeeNotFoundException, IOException;

    Employee updateEmployee(String currentUsername, String newFirstName, String newLastName, String newUsername, String newFunction, Long newPhoneNumber, Long newCicNumber,
                            String Role, boolean isEnabled, boolean isNotlocked, MultipartFile profileImage) throws UsernameExistException, EmployeeNotFoundException, IOException;

    void deleteEmployee(long employeeIdDataBase);

    void resetPassword(String username) throws MessagingException;

    Employee updateProfileImage(String username, MultipartFile profileImage) throws UsernameExistException, EmployeeNotFoundException, IOException;

    String confirmToken(String token);
}
