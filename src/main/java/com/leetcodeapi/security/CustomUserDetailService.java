package com.leetcodeapi.security;


import com.leetcodeapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * <h2>Description</h2>
 * This class provides a custom implementation of the Spring Security UserDetailsService to load user details from the UserRepository.
 *
 *  <h2>Method definition</h2>
 *  <p>{@link #loadUserByUsername}: Loads user details from the UserRepository using the provided mobile number as the username.</p>
 * @author Utkarsh Jaiswal
 * @version 1.0
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details from the UserRepository using  the username
     * @param username : the username of the user to load
     * @return  the UserDetails object containing the user's details
     * @throws UsernameNotFoundException  if the user is not found in the repository
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    }
}
