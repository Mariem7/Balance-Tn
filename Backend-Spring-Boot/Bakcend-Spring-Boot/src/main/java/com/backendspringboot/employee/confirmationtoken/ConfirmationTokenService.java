package com.backendspringboot.employee.confirmationtoken;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    //method to save the token
    public void saveConfirmationToken(ConfirmationToken token){

        confirmationTokenRepository.save(token);
    }

    //method to get token
    public Optional<ConfirmationToken> getToken(String token){

        return confirmationTokenRepository.findByToken(token);
    }

    //method to set the confirmed time of the token
    public int setConfirmedAt(String token){
        return confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
