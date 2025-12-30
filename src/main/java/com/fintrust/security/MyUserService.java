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
//      	System.out.println("user not found");
        throw new UsernameNotFoundException("User not found");
    }
               

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name()) // ROLE_ADMIN / ROLE_USER
                .build();
    }
    
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//    	System.out.println("Inside method " + username);
//        User user = userService.getUserByUserName(username);
//        if (user == null) {
//          	System.out.println("user not found");
//            throw new UsernameNotFoundException("User not found");
//        }
//        System.out.println("after method " + user);
//        String role = user.getRole().name();
//        if (!role.startsWith("ROLE_")) {
//            role = "ROLE_" + role;
//        }
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getEmail(),
//                user.getPassword(),        // MUST be BCrypt encrypted
//                AuthorityUtils.createAuthorityList("ROLE_USER")
//        );
//    }
}
