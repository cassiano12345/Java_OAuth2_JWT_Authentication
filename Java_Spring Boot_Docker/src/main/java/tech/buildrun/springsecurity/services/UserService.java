package tech.buildrun.springsecurity.services;

import org.springframework.stereotype.Service;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =========================================================
    // BUSCAR USUÁRIO PELO ID
    // =========================================================

    public User findById(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado")
                );
    }

    // =========================================================
    // BUSCAR USUÁRIO PELO USERNAME EXATO
    // =========================================================

    public User findByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado")
                );
    }

    // =========================================================
    // PESQUISAR USUÁRIOS PELO USERNAME
    // =========================================================

    public List<User> searchByUsername(String username) {

        if (username == null || username.isBlank()) {
            return List.of();
        }

        return userRepository.findByUsernameContainingIgnoreCase(
                username.trim()
        );
    }

    // =========================================================
    // LISTAR TODOS OS USUÁRIOS
    // =========================================================

    public List<User> findAll() {

        return userRepository.findAll();
    }
}
