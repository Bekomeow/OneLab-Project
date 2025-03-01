package org.example.repository.mapper;

import org.example.dto.UserDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserRowMapper implements RowMapper<UserDTO> {
    @Override
    public UserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        String ticketIdsStr = rs.getString("ticket_ids");

        List<Long> ticketIds = (ticketIdsStr != null && !ticketIdsStr.equalsIgnoreCase("null") && !ticketIdsStr.equalsIgnoreCase("[null]"))
                ? Arrays.stream(ticketIdsStr.replaceAll("[{}\\[\\]]", "").split(",")) // Убираем `{}`, `[]`
                .map(String::trim)
                .filter(s -> !s.isEmpty() && !s.equalsIgnoreCase("null")) // Убираем пустые и "null"
                .map(Long::parseLong)
                .collect(Collectors.toList())
                : List.of();

        return UserDTO.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .ticketIds(ticketIds)
                .build();
    }
}
