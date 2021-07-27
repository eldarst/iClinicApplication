package kg.iclinic.application.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsServiceOne {
    public UserDetails loadUserByUsername(String username);

}
