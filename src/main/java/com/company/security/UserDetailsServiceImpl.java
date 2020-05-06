package com.company.security;

import com.company.entities.CustomUser;
import com.company.repo.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {
        CustomUser customUser = userService.findByLogin(login);
        if (customUser == null)
            throw new UsernameNotFoundException(login + " not found");

        List<GrantedAuthority> roles =
                Collections.singletonList(
                        new SimpleGrantedAuthority(customUser.getRole().toString()));

        CustomUserDetails customUserDetail=new CustomUserDetails();
        customUserDetail.setUser(customUser);
        customUserDetail.setAuthorities(roles);

        return customUserDetail;
    }
}