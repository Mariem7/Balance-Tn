package com.backendspringboot.employee.BruteForceAttackPrevent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener {
    private LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    //method that will listen to the authentication process
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof String){
            String username = (String) event.getAuthentication().getPrincipal();
            //if the employee fail to log in, we will add it to the cache
            loginAttemptService.addEmployeeToLoginAttemptCahce(username);
        }
    }

}
