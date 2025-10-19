package com.Project_INTELLCAP.Infinitum_Art.DTOsGlobal;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private String message;
    private String timestamp;

    // You can also add a constructor for just message and status for convenience
    public MessageResponse(String message) {
        this.message = message;
         this.timestamp = LocalDateTime.now().toString();
    }
}
