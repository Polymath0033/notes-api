package org.polymath.noteapi.service;

import org.polymath.noteapi.models.UserPrincipal;
import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Users users = userRepo.findUserByEmail(userEmail);
        if(users == null) {
            throw new UsernameNotFoundException(userEmail);
//                    ||throw new UsernameNotFoundException("user not found");
        }
        return new  UserPrincipal(users);
    }
}
