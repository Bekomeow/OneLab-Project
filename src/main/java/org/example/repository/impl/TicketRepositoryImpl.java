package org.example.repository.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.TicketDTO;
import org.example.enums.TicketStatus;
import org.example.repository.TicketRepository;
import org.example.repository.mapper.TicketRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TicketRowMapper ticketRowMapper;

    @Override
    public TicketDTO createTicket(Long userId, Long eventId) {
        UUID ticketNumber = UUID.randomUUID();
        String sql = "INSERT INTO tickets (ticket_number, user_id, event_id, status) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ticketNumber.toString());
            ps.setLong(2, userId);
            ps.setLong(3, eventId);
            ps.setString(4, TicketStatus.ACTIVE.name());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            Long generatedId = keyHolder.getKey().longValue();
            return TicketDTO.builder()
                    .id(generatedId)
                    .ticketNumber(ticketNumber)
                    .userId(userId)
                    .eventId(eventId)
                    .status(TicketStatus.ACTIVE)
                    .build();
        }
        throw new RuntimeException("Не удалось создать билет");
    }


    @Override
    public void cancelTicket(UUID ticketNumber) {
        String sql = "UPDATE tickets SET status = ? WHERE ticket_number = ?";
        jdbcTemplate.update(sql, TicketStatus.CANCELLED.name(), ticketNumber.toString());
    }

    @Override
    public Optional<TicketDTO> getTicketByNumber(UUID ticketNumber) {
        String sql = "SELECT * FROM tickets WHERE ticket_number = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, ticketNumber.toString()).stream().findFirst();
    }

    @Override
    public Optional<TicketDTO> findById(Long id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, id).stream().findFirst();
    }

    @Override
    public List<TicketDTO> getTicketsByUser(Long userId) {
        String sql = "SELECT * FROM tickets WHERE user_id = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, userId);
    }

    @Override
    public List<TicketDTO> getTicketsByEvent(Long eventId) {
        String sql = "SELECT * FROM tickets WHERE event_id = ?";
        return jdbcTemplate.query(sql, ticketRowMapper, eventId);
    }

    @Override
    public int deleteTicketsByEventAndUser(Long eventId, Long userId) {
        String sql = "DELETE FROM tickets WHERE event_id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, eventId, userId);
    }
}

