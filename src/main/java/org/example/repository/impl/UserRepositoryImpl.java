package org.example.repository.impl;

import org.example.dto.UserDTO;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, UserDTO> users = new HashMap<>();

    @Override
    public Optional<UserDTO> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<UserDTO> save(UserDTO user) {
        users.put(user.getId(), user);
        return Optional.ofNullable(user);
    }
}
