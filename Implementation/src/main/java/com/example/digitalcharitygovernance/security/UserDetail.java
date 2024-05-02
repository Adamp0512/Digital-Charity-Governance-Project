package com.example.digitalcharitygovernance.security;

import com.example.digitalcharitygovernance.models.User;
import com.example.digitalcharitygovernance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetail implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("Username not found");
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());
        Set<GrantedAuthority> authorities= new HashSet<>();
        authorities.add(authority);


        return new org.springframework.security.core.userdetails.User(username,user.getPassword(),authorities);

    }
}
