package com.fintrust.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.fintrust.service.UserService;
import org.springframework.security.core.authority.AuthorityUtils;

public class MyUserService implements UserDetailsService {

    private UserService userService;

    // Injected by Spring XML
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        com.fintrust.model.User u = userService.getUserByUserName(username);

        if (u == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        String role = u.getRole().name();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return new User(
                u.getEmail(),
                u.getPassword(),
                AuthorityUtils.createAuthorityList(role)
        );
    }
}
