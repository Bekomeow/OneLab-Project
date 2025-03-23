package com.example.commonlibrary.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventRegisterResponse {
    private Long id;
    private String username;
    private String eventTitle;
}
