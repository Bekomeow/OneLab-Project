package com.example.approvalservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {
    @GetMapping("/auth/users/{username}/email")
    String getEmailByUsername(@PathVariable String username, @RequestHeader("Authorization") String token);
}
