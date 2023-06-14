package com.leetcodeapi.repository;

import com.leetcodeapi.entities.Task;
import com.leetcodeapi.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {

    Page<Task> findTasksByUser(User user,Pageable pageable);

    Boolean existsByProblemId(Long problem_id);
}
