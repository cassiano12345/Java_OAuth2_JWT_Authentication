package tech.buildrun.springsecurity.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    public AuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Utilizador não encontrado."));
    }

}
