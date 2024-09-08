package com.leetcodeapi.controller;

import com.leetcodeapi.dto.LoginRequest;
import com.leetcodeapi.dto.NewPassword;
import com.leetcodeapi.dto.TokenResponse;
import com.leetcodeapi.dto.UserDto;
import com.leetcodeapi.exception.WrongInputException;
import com.leetcodeapi.services.MailService;
import com.leetcodeapi.services.PasswordResetTokenService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordResetTokenService tokenService;
    private final ModelMapper modelMapper;

    public AuthController(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService, AuthenticationManager authenticationManager, UserService userService, PasswordResetTokenService tokenService, MailService mailService, ModelMapper modelMapper) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
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

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String username) {
        userService.forgotPassword(username);
        return new ResponseEntity<>("Mail sent successfully", HttpStatus.OK);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> showResetPasswordPage(@RequestParam String token) {
        if (!tokenService.validateToken(token)) {
            return new ResponseEntity<>("Invalid or Expired Reset Link", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Reset password page", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody NewPassword newPassword) {
        userService.resetPassword(token, newPassword.getNewPassword());
        return new ResponseEntity<>("Password successfully reset", HttpStatus.OK);
    }

}
