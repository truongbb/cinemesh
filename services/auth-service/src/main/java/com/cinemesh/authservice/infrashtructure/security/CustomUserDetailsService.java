package com.cinemesh.authservice.infrashtructure.security;

import com.cinemesh.authservice.infrashtructure.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional // Để load được Lazy collection (Role) nếu có
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Tìm user theo email
        return userRepository.findByEmail(email)
                .map(CustomUserDetails::mapFrom) // Convert Entity -> UserDetails
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
