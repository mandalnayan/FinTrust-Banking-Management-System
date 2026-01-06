package com.fintrust.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.AuthorityUtils;

import com.fintrust.service.UserService;
import com.fintrust.model.User;

public class MyUserService implements UserDetailsService {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userService.getUserByUserName(username);
        if (user == null) {

        throw new UsernameNotFoundException("User not found");
    }
               

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name()) // ROLE_ADMIN / ROLE_USER
                .build();
    }
    
}
