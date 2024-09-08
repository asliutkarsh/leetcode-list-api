package com.leetcodeapi.services.impl;

import com.leetcodeapi.dto.UserDto;
import com.leetcodeapi.entities.User;
import com.leetcodeapi.exception.DuplicateEntryException;
import com.leetcodeapi.exception.LeetcodeIdNotFoundException;
import com.leetcodeapi.exception.ResourceNotFoundException;
import com.leetcodeapi.exception.WrongTokenException;
import com.leetcodeapi.repository.UserRepository;
import com.leetcodeapi.services.LeetcodeService;
import com.leetcodeapi.services.MailService;
import com.leetcodeapi.services.PasswordResetTokenService;
import com.leetcodeapi.services.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LeetcodeService leetcodeService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenService tokenService;
    private final MailService mailService;

    public UserServiceImpl(UserRepository userRepository, LeetcodeService leetcodeService, ModelMapper modelMapper, PasswordEncoder passwordEncoder, PasswordResetTokenService tokenService, MailService mailService) {
        this.userRepository = userRepository;
        this.leetcodeService = leetcodeService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.mailService = mailService;
    }


    @Override
    public Long createUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new DuplicateEntryException("This user-name already exists");
        }
        if (!leetcodeService.existByuserName(userDto.getUsername())) {
            throw new LeetcodeIdNotFoundException();
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setTotalPoints(0L);
        user.setDailyPoints(0L);
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!Objects.equals(user.getUsername(), userDto.getUsername()) && userDto.getUsername() != null) {
            if (userRepository.existsByUsername(userDto.getUsername())) {
                throw new DuplicateEntryException("This user-name already exists");
            }
            if (!leetcodeService.existByuserName(userDto.getUsername())) {
                throw new LeetcodeIdNotFoundException();
            }

            user.setUsername(userDto.getUsername());
        }
        if (!Objects.equals(user.getName(), userDto.getName()) && userDto.getName() != null) {

            user.setName(userDto.getName());
        }
        if (!Objects.equals(user.getPassword(), passwordEncoder.encode(userDto.getPassword())) && userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        if (userDto.getDailyPoints() == 0) {
            user.setDailyPoints(0L);
        }

        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserDto.class);
    }



    @Override
    public List<UserDto> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return modelMapper.map(user, UserDto.class);

    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        userRepository.delete(user);

    }

    @Override
    public void forgotPassword(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        String token = tokenService.createToken(user);
        mailService.sendForgetPasswordMail(user.getEmail(),username, token);
    }

    @Transactional
    @Override
    public void resetPassword(String token, String newPassword) {
        if (tokenService.validateToken(token)) {
            User user = tokenService.getUserByToken(token);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            tokenService.deleteToken(token);
        }else {
            throw new WrongTokenException();
        }
    }

}
