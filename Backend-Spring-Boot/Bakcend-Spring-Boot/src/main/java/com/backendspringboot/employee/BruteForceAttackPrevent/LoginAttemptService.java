package com.backendspringboot.employee.BruteForceAttackPrevent;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

//this class will help us to count the number of time that a user login
//to prevent from Brute Force Attack
@Service
public class LoginAttemptService {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    private static final int ATTEMPT_INCREMENT = 1;
    private LoadingCache<String,Integer> loginAttemptCache;

    public LoginAttemptService(){
        super();
        //maximume size is the number of user inside the cache
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES)
                .maximumSize(300).build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    //method to delete employee from the loginCache
    public void evictEmployeeFromLoginAttemptCache(String username){
        loginAttemptCache.invalidate(username);
    }

    //method to add employee to the loginCache
    public void addEmployeeToLoginAttemptCahce(String username) {
        int attempts =0;
        //we will have the total number of attempts
        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        loginAttemptCache.put(username,attempts);

    }

    public boolean hasExceededMaxAttempts(String username){
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }



}
