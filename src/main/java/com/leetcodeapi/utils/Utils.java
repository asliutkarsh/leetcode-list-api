package com.leetcodeapi.utils;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class Utils {

//    public static String generateUniqueCode() {
//        return UUID.randomUUID().toString().substring(0,8).toUpperCase();
//    }

    public static String getAuthenticatedUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
