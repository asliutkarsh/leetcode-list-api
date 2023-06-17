package com.leetcodeapi.controller;

import com.leetcodeapi.dto.LoginRequest;
import com.leetcodeapi.dto.TokenResponse;
import com.leetcodeapi.dto.UserDto;
import com.leetcodeapi.exception.WrongInputException;
import com.leetcodeapi.services.UserService;
import com.leetcodeapi.utils.JwtTokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;

    private final UserDetailsService userDetailsService;

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final ModelMapper modelMapper;

    public AuthController(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService, AuthenticationManager authenticationManager, UserService userService, ModelMapper modelMapper) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> createToken(@RequestBody LoginRequest request) {
        authenticate(request.getUsername(),request.getPassword());
        UserDetails userDetails=userDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtTokenUtil.generatedToken(userDetails);

        TokenResponse response = new TokenResponse();
        response.setToken(token);
        response.setUser(modelMapper.map(userDetails,UserDto.class));
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    private void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,password);
        try {
            authenticationManager.authenticate(authenticationToken);
        }catch (BadCredentialsException e){
            throw  new WrongInputException();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Long> registerUser(@RequestBody UserDto userDto) {
        Long registeredUser = userService.createUser(userDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }


}
