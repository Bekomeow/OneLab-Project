package com.example.commonlibrary.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRegisterResponse {
    private Long id;
    private String username;
    private String eventTitle;
}
