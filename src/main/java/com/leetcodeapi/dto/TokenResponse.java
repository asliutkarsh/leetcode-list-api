package com.leetcodeapi.dto;

import lombok.Data;

@Data

public class TokenResponse {
    private String token;

    private UserDto user;
}
