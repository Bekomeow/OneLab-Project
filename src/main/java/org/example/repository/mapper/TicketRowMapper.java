package org.example.repository.mapper;

import org.example.dto.TicketDTO;
import org.example.enums.TicketStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class TicketRowMapper implements RowMapper<TicketDTO> {
    @Override
    public TicketDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return TicketDTO.builder()
                .id(rs.getLong("id"))
                .ticketNumber(UUID.fromString(rs.getString("ticket_number")))
                .userId(rs.getLong("user_id"))
                .eventId(rs.getLong("event_id"))
                .status(TicketStatus.valueOf(rs.getString("status")))
                .build();
    }
}
