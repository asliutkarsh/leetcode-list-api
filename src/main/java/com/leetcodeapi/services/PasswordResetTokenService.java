package com.leetcodeapi.services;

import com.leetcodeapi.entities.User;

public interface PasswordResetTokenService {

    String createToken(User user);
    boolean validateToken(String token);
    void deleteToken(String token);
    User getUserByToken(String token);
}
