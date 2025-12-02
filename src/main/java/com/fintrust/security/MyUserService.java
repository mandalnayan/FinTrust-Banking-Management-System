package com.fintrust.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.fintrust.service.UserService;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;

public class MyUserService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    	UserService userService = new UserServiceImpl();
        com.fintrust.model.User user = userService.getUserByUserName(username);
        
        
        if (user == null) {
            NotificationUtil.showInstant("error", "Server error. \nPlease try again!");
            throw new UsernameNotFoundException("User not found");
        }

        System.out.println("User Data\n" + user);

        // Use role from DB
        String role = user.getRole().name().toUpperCase();
        if (role == null || role.isEmpty()) {
        	NotificationUtil.showInstant("error", "Server error. \nPlease try again!");
//            role = "ROLE_USER";
        	return null;
        }

        return new User(
            user.getEmail(),
            user.getPassword(),
            toGrantedAuthorities(new String[] { role })
        );
    }

    private Collection<? extends GrantedAuthority> toGrantedAuthorities(String[] roles) {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>(roles.length);
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
