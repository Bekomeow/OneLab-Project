package org.example.repository.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserDTO;
import org.example.repository.UserRepository;
import org.example.repository.mapper.UserRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public Optional<UserDTO> findById(Long id) {
        String sql = "SELECT u.id, u.name, ARRAY_AGG(t.id) AS ticket_ids FROM users u " +
                "LEFT JOIN tickets t ON u.id = t.user_id WHERE u.id = ? GROUP BY u.id, u.name";
        return jdbcTemplate.query(sql, userRowMapper, id).stream().findFirst();
    }

    @Override
    public Optional<UserDTO> save(UserDTO user) {
        String sql = "INSERT INTO users (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            Long generatedId = keyHolder.getKey().longValue();
            return Optional.of(UserDTO.builder()
                    .id(generatedId)
                    .name(user.getName())
                    .ticketIds(List.of())
                    .build());
        }
        return Optional.empty();
    }

}
