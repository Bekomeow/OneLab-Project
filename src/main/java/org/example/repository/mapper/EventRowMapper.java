package org.example.repository.mapper;

import org.example.dto.EventDTO;
import org.example.enums.EventStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class EventRowMapper implements RowMapper<EventDTO> {
    @Override
    public EventDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EventDTO.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .date(rs.getObject("date", LocalDateTime.class))
                .maxParticipants(rs.getInt("max_participants"))
                .status(EventStatus.valueOf(rs.getString("status")))
                .build();
    }
}
