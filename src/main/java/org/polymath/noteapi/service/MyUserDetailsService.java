package org.polymath.noteapi.service;

import org.polymath.noteapi.models.UserPrincipal;
import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Users users = userRepo.findUserById(UUID.fromString(userId));
        if(users == null) {
            throw new UsernameNotFoundException(userId);
        }
        return new  UserPrincipal(users);
    }
}
