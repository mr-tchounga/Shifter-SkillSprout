package com.shifter.shifter_back.services.auth;

import com.shifter.shifter_back.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService {
    public Optional<User> findByEmail(String email);
    public User findById(Long id);
    public User createUser(User user);
    public boolean isEmailExisted(String email);
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
