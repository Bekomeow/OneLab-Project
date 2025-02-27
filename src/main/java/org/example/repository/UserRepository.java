package org.example.repository;

import org.example.dto.UserDTO;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<UserDTO> findById(Long id);
    Optional<UserDTO> save(UserDTO user);
}

