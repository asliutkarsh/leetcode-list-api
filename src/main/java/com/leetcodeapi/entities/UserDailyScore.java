package com.leetcodeapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_daily_scores",uniqueConstraints = @UniqueConstraint(name = "user_daily_score_date_unique",columnNames = {"date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDailyScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Long score;

    public UserDailyScore(User user, LocalDate date, Long score) {
        this.user = user;
        this.date = date;
        this.score = score;
    }
}
