package com.leetcodeapi.services;

import com.leetcodeapi.dto.UserDto;
import com.leetcodeapi.entities.User;
import com.leetcodeapi.exception.DuplicateEntryException;
import com.leetcodeapi.exception.LeetcodeIdNotFoundException;
import com.leetcodeapi.exception.ResourceNotFoundException;
import com.leetcodeapi.repository.UserRepository;
import com.leetcodeapi.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl underTestService;

    @Mock private UserRepository userRepository;

    @Mock private LeetcodeService leetcodeService;

    @Mock
    private ModelMapper modelMapper;
    @Mock private PasswordEncoder passwordEncoder;


    @Test
    void createUser() {
        //Given
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setUsername("john");
        userDto.setPassword("password");

        User user = new User("John Doe", "john", "password");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(leetcodeService.existByuserName(anyString())).thenReturn(true);
        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        user.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);


        //When
        Long userId = underTestService.createUser(userDto);

        //Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(userId).isEqualTo(1L);
        assertThat(capturedUser.getName()).isEqualTo(userDto.getName());
        assertThat(capturedUser.getUsername()).isEqualTo(userDto.getUsername());
        assertThat(capturedUser.getPassword()).isEqualTo("encodedPassword");

    }

    @Test
    void createUser_DuplicateUsername_ThrowsDuplicateEntryException() {
        //Given
        UserDto userDto = new UserDto();
        userDto.setUsername("john");
        userDto.setPassword("password");

        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThatThrownBy(() -> underTestService.createUser(userDto))
                .isInstanceOf(DuplicateEntryException.class)
                .hasMessageContaining("This user-name already exists");

        verify(userRepository,never()).save(any());
    }

    @Test
    void createUser_LeetcodeIdNotFound_ThrowsLeetcodeIdNotFoundException() {
        //Given
        UserDto userDto = new UserDto();
        userDto.setUsername("john");
        userDto.setPassword("password");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(leetcodeService.existByuserName(anyString())).thenReturn(false);

        assertThatThrownBy(() -> underTestService.createUser(userDto))
                .isInstanceOf(LeetcodeIdNotFoundException.class);

        verify(userRepository,never()).save(any());
    }

    @Test
    void updateUser() {
        //Given
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUsername");
        existingUser.setPassword("oldPassword");
        existingUser.setName("Old Name");
        existingUser.setDailyPoints(5L);

        UserDto userDto = new UserDto();
        userDto.setUsername("john");
        userDto.setPassword("newPassword");
        userDto.setName("John Doe");
        userDto.setDailyPoints(0L);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername(userDto.getUsername());
        updatedUser.setPassword(userDto.getPassword());
        updatedUser.setName(userDto.getName());
        updatedUser.setDailyPoints(userDto.getDailyPoints());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(leetcodeService.existByuserName(anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);

        //When
        UserDto result = underTestService.updateUser(userDto, userId);

        //Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertThat(result).isEqualTo(userDto);
        assertThat(capturedUser.getId()).isEqualTo(userId);
        assertThat(capturedUser.getUsername()).isEqualTo(userDto.getUsername());
        assertThat(capturedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(capturedUser.getName()).isEqualTo(userDto.getName());
        assertThat(capturedUser.getDailyPoints()).isEqualTo(userDto.getDailyPoints());

    }

    @Test
    void updateUser_UserNotFound_ThrowsUserNotFoundException() {
        //Given
        Long userId = 1L;

        UserDto userDto = new UserDto();
        userDto.setUsername("john");
        userDto.setPassword("newPassword");
        userDto.setName("John Doe");
        userDto.setDailyPoints(0L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.updateUser(userDto, userId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository,never()).save(any());
    }

    @Test
    void updateUser_DuplicateUsername_ThrowsDuplicateEntryException() {
        //Given
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUsername");
        existingUser.setPassword("oldPassword");
        existingUser.setName("Old Name");
        existingUser.setDailyPoints(5L);

        UserDto userDto = new UserDto();
        userDto.setUsername("john");
        userDto.setPassword("newPassword");
        userDto.setName("John Doe");
        userDto.setDailyPoints(0L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThatThrownBy(() -> underTestService.updateUser(userDto, userId))
                .isInstanceOf(DuplicateEntryException.class)
                .hasMessageContaining("This user-name already exists");

        verify(userRepository,never()).save(any());
    }

    @Test
    void updateUser_LeetcodeIdNotFound_ThrowsLeetcodeIdNotFoundException() {
        //Given
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUsername");
        existingUser.setPassword("oldPassword");
        existingUser.setName("Old Name");
        existingUser.setDailyPoints(5L);

        UserDto userDto = new UserDto();
        userDto.setUsername("john");
        userDto.setPassword("newPassword");
        userDto.setName("John Doe");
        userDto.setDailyPoints(0L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(leetcodeService.existByuserName(anyString())).thenReturn(false);

        assertThatThrownBy(() -> underTestService.updateUser(userDto, userId))
                .isInstanceOf(LeetcodeIdNotFoundException.class);

        verify(userRepository,never()).save(any());
    }


//Todo
    @Test
    void getAllUser() {
        //Given

        //When

        //Then

    }

    @Test
    void getUserById_ExistingUserId_ReturnsUserDto() {
        // Given
        Long userId = 1L;
        String username = "john";
        String password = "password";
        String name = "John Doe";

        User user = new User(name,username, password);
        user.setId(userId);

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(userId);
        expectedUserDto.setName(name);
        expectedUserDto.setUsername(username);
        expectedUserDto.setPassword(password);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(expectedUserDto);

        // When
        UserDto result = underTestService.getUserById(userId);

        // Then
        assertThat(result).isEqualTo(expectedUserDto);
    }

    @Test
    void getUserById_NonExistingUserId_ThrowsResourceNotFoundException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void getUserByUsername_ExistingUserName_ReturnsUserDto() {
        // Given
        Long userId = 1L;
        String username = "john";
        String password = "password";
        String name = "John Doe";

        User user = new User(name,username, password);
        user.setId(userId);

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(userId);
        expectedUserDto.setName(name);
        expectedUserDto.setUsername(username);
        expectedUserDto.setPassword(password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(expectedUserDto);

        // When
        UserDto result = underTestService.getUserByUsername(username);

        // Then
        assertThat(result).isEqualTo(expectedUserDto);

    }

    @Test
    void getUserByUsername_ExistingUserName_ThrowsResourceNotFoundException() {
        String username = "john";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTestService.getUserByUsername(username))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteUser_ExistingUserId_ReturnsUserDto() {
        //Given
        Long userId = 1L;
        String username = "john";
        String password = "password";
        String name = "John Doe";

        User user = new User(name,username, password);
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        //When
        underTestService.deleteUser(userId);

        //Then
        verify(userRepository).delete(user);

    }

    @Test
    void deleteUser_NonExistingUserId_ThrowsResourceNotFoundException() {
        //Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        // When
        assertThatThrownBy(() -> underTestService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class);
        // Then
        verify(userRepository,never()).delete(any());
    }

}