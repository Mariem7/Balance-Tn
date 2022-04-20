package com.backendspringboot.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    Employee findEmployeeByUsername(String username);

    //update the enable value in the database
    @Transactional
    @Modifying
    @Query("update Employee a set a.isEnabled = TRUE where a.username =?1")
    int enableEmployee(String username);
}
