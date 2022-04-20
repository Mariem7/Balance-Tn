package com.backendspringboot.employee.BruteForceAttackPrevent;

import com.backendspringboot.employee.EmployeePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener {
    private LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof EmployeePrincipal){
            EmployeePrincipal employee = (EmployeePrincipal) event.getAuthentication().getPrincipal();
            //when the employee is authenticated successfully, we need to remove it from the cache
            loginAttemptService.evictEmployeeFromLoginAttemptCache(employee.getUsername());
        }
    }
}
