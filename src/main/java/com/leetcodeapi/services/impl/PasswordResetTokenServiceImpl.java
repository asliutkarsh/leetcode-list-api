package com.leetcodeapi.services.impl;

import com.leetcodeapi.entities.PasswordResetToken;
import com.leetcodeapi.entities.User;
import com.leetcodeapi.exception.ResourceNotFoundException;
import com.leetcodeapi.exception.WrongTokenException;
import com.leetcodeapi.repository.PasswordResetTokenRepository;
import com.leetcodeapi.repository.UserRepository;
import com.leetcodeapi.services.PasswordResetTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository tokenRepository;

    private final UserRepository userRepository;

    public PasswordResetTokenServiceImpl(PasswordResetTokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }
    public String createToken(User user) {
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        tokenRepository.save(token);
        return token.getToken();
    }

    public boolean validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new WrongTokenException());
        return resetToken.getExpiryDate().isAfter(LocalDateTime.now());
    }

    public void deleteToken(String token) {
        tokenRepository.deleteByToken(token);
    }

    @Override
    public User getUserByToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new WrongTokenException());
        return resetToken.getUser();
    }
}
