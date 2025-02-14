package org.polymath.noteapi.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserPrincipal implements UserDetails, OAuth2User {
    private final Users users;
    Map<String, Object> attributes;

    public UserPrincipal(Users users) {
        this.users = users;
    }

    public UserPrincipal(Users user, Map<String, Object> attributes) {
        this.users = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public String getUsername() {
      return users.getEmail();
    }


    @Override
    public String getName() {
        return users.getEmail();
    }
}
