package com.leetcodeapi.repository;

import com.leetcodeapi.entities.Task;
import com.leetcodeapi.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository underTestRepo;

    @BeforeEach
    void setUp() {
        underTestRepo.deleteAll();
    }

    @Test
    void shouldSaveTask(){
        //Given
        Task task = new Task();
        task.setProblemId(1L);
        task.setTitle("test_title");
        task.setDifficulty("test_difficulty");
        task.setLink("test_url");
        task.setNotes("test_notes");
        task.setUser(new User());
        //When
        var actual = underTestRepo.save(task);

        //Then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getProblemId()).isEqualTo(task.getProblemId());
        assertThat(actual.getTitle()).isEqualTo(task.getTitle());
        assertThat(actual.getDifficulty()).isEqualTo(task.getDifficulty());
        assertThat(actual.getLink()).isEqualTo(task.getLink());
        assertThat(actual.getNotes()).isEqualTo(task.getNotes());
        assertThat(actual.getUser()).isEqualTo(task.getUser());
    }

    @Test
    void shouldDeleteTask(){
        //Given
        Task task = new Task();
        task.setProblemId(1L);
        task.setTitle("test_title");
        task.setDifficulty("test_difficulty");
        task.setLink("test_url");
        task.setNotes("test_notes");
        task.setUser(new User());
        var savedTask = underTestRepo.save(task);
        //When
        underTestRepo.delete(savedTask);
        //Then
        assertThat(underTestRepo.findById(savedTask.getId())).isEmpty();
    }

}