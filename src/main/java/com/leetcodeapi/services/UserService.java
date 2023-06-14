package com.leetcodeapi.services;

import com.leetcodeapi.dto.UserDto;

import java.util.List;

public interface UserService {

    Long registerNewUser(UserDto userDto);
    Long createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto,Long userId);
    List<UserDto> getAllUser();
    UserDto getUserById(Long userId);
    UserDto getUserByUsername(String username);
    void deleteUser(Long userId);


}
