package com.leetcodeapi.controller;

import com.leetcodeapi.dto.ApiResponse;
import com.leetcodeapi.dto.UserDto;
import com.leetcodeapi.services.UserService;
import com.leetcodeapi.utils.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<Long> createUser( @RequestBody UserDto userDto) {
        Long createUserId = userService.createUser(userDto);
        return new ResponseEntity<>(createUserId, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser( @PathVariable("userId")Long userId, @RequestBody UserDto userDto){
        UserDto updatedUser = userService.updateUser(userDto,userId);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Long userId){
        userService.deleteUser(userId);
        return new ResponseEntity<>(new ApiResponse("User Deleted Successfully",true),HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<UserDto>> fetchAllUsers(){
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> fetchUserById(@PathVariable("userId") Long userId)  {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserDto> getUser() {
        UserDto user = userService.getUserByUsername(Utils.getAuthenticatedUserName());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


}
