package com.leetcodeapi.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
    @Table(name = "tasks",uniqueConstraints = @UniqueConstraint(name = "task_problem_unique",columnNames = "problem_id"))
    @Getter
    @Setter
    @ToString
    @RequiredArgsConstructor
    @AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "problem_id",nullable = false)
    private Long problemId;
    private String title;
    private String difficulty;
    private String link;
    private Long points;
    private Timestamp timestamp;
    @Column(length = 1000000)
    private String notes;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Task task = (Task) o;
        return getId() != null && Objects.equals(getId(), task.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
