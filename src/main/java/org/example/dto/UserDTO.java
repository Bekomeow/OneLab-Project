package org.example.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private List<Long> ticketIds;
}

