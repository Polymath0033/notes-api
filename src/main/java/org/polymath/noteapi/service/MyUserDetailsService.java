package org.polymath.noteapi.service;

import org.polymath.noteapi.models.UserPrincipal;
import org.polymath.noteapi.models.Users;
import org.polymath.noteapi.repositories.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

   private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users users = userRepo.findUserByEmail(email).orElseThrow(()->new UsernameNotFoundException(email));
        return new  UserPrincipal(users);
    }
}
