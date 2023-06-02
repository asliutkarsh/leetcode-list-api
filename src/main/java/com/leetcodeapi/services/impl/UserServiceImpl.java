package com.leetcodeapi.services.impl;

import com.leetcodeapi.dto.UserDto;
import com.leetcodeapi.entities.User;
import com.leetcodeapi.exception.DuplicateEntryException;
import com.leetcodeapi.exception.ResourceNotFoundException;
import com.leetcodeapi.repository.UserRepository;
import com.leetcodeapi.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Long createUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())){
            throw new DuplicateEntryException();
        }
        User user = modelMapper.map(userDto,User.class);
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User","id",userId));

        if (!Objects.equals(user.getUsername(), userDto.getUsername()) && userDto.getUsername() != null){
            if (userRepository.existsByUsername(userDto.getUsername())){
                throw new DuplicateEntryException();
            }

            user.setUsername(userDto.getUsername());
        }
        if (!Objects.equals(user.getName(), userDto.getName()) && userDto.getName() != null){

            user.setName(userDto.getName());
        }
        if (!Objects.equals(user.getPassword(), userDto.getPassword()) && userDto.getPassword() != null){
            user.setPassword(userDto.getPassword());
        }

        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser,UserDto.class);

    }

    @Override
    public List<UserDto> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user,UserDto.class)).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User","id",userId));
        return modelMapper.map(user,UserDto.class);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User","id",userId));
        userRepository.delete(user);

    }
}
