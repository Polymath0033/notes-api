package org.polymath.noteapi.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String email;
    public CustomAuthenticationToken(String principal, String credentials, String email) {
        super(principal, credentials);
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

}
