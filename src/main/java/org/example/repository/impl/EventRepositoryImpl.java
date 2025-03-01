package org.example.repository.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.EventDTO;
import org.example.repository.EventRepository;
import org.example.repository.mapper.EventRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {
    private final JdbcTemplate jdbcTemplate;
    private final EventRowMapper eventRowMapper;

    @Override
    public Optional<EventDTO> findById(Long id) {
        String sql = "SELECT id, name, date, max_participants, status FROM events WHERE id = ?";
        return jdbcTemplate.query(sql, eventRowMapper, id).stream().findFirst();
    }

    @Override
    public List<EventDTO> findAll() {
        String sql = "SELECT id, name, date, max_participants, status FROM events";
        return jdbcTemplate.query(sql, eventRowMapper);
    }

    @Override
    public Optional<EventDTO> save(EventDTO event) {
        String sql = "INSERT INTO events (name, date, max_participants, status) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, event.getName());
            ps.setTimestamp(2, Timestamp.valueOf(event.getDate()));
            ps.setInt(3, event.getMaxParticipants());
            ps.setString(4, event.getStatus().name());
            return ps;
        }, keyHolder);

        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            event.setId(keyHolder.getKey().longValue());
            return Optional.of(event);
        }
        return Optional.empty();
    }

}
