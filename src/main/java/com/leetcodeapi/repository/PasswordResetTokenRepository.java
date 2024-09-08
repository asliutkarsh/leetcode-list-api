package com.leetcodeapi.repository;

import com.leetcodeapi.entities.PasswordResetToken;
import com.leetcodeapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {

    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
    void deleteByToken(String token);
}
