package com.leetcodeapi.repository;

import com.leetcodeapi.entities.UserDailyScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserDailyScoreRepository extends JpaRepository<UserDailyScore,Long> {

    Optional<UserDailyScore> findByUser_IdAndDate(Long user_id, LocalDate date);
}
