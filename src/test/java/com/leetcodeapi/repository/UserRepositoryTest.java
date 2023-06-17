package com.leetcodeapi.repository;

import com.leetcodeapi.AbstractTestContainer;
import com.leetcodeapi.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends AbstractTestContainer {

    @Autowired
    private UserRepository underTestRepo;

    @BeforeEach
    void setUp() {
        underTestRepo.deleteAll();
    }

    @Test
    void shouldSaveUser(){
        //Given
        User user = new User();
        user.setName("test_name");
        user.setUsername("test_username");
        user.setPassword("test_password");

        //When
        var actual = underTestRepo.save(user);

        //Then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(user.getName());
        assertThat(actual.getUsername()).isEqualTo(user.getUsername());
        assertThat(actual.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void deleteUser(){
        //Given
        User user = new User();
        user.setName("test_name");
        user.setUsername("test_username");
        user.setPassword("test_password");
        User savedUser = underTestRepo.save(user);

        //When
        underTestRepo.delete(user);

        //Then
        assertThat(underTestRepo.findById(savedUser.getId())).isEmpty();
    }
    @Test
    void findById(){
        //Given
        User user = new User();
        user.setName("test_name");
        user.setUsername("test_username");
        user.setPassword("test_password");
        User savedUser = underTestRepo.save(user);

        //When
        var actual = underTestRepo.findById(savedUser.getId());

        //Then
        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).isEqualTo(savedUser);
    }

    @Test
    void findAll(){
        //Given
        User user = new User();
        user.setName("test_name");
        user.setUsername("test_username");
        user.setPassword("test_password");
        User savedUser = underTestRepo.save(user);

        //When
        var actual = underTestRepo.findAll();

        //Then
        assertThat(actual).isNotEmpty();
        assertThat(actual).contains(savedUser);
    }

    @Test
    void existsByUsername() {
        //Given
        String username = "test_username";
        User user = new User();
        user.setName("test_name");
        user.setUsername(username);
        user.setPassword("test_password");
        underTestRepo.save(user);

        //When
        var actual = underTestRepo.existsByUsername(username);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsByUsername_Fails() {
        //Given
        String username = "test_username";

        //When
        var actual = underTestRepo.existsByUsername(username);

        //Then
        assertThat(actual).isFalse();
    }

}