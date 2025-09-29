package com.securityspring.infrastructure.adapters.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogDto {
    private Long id;
    private String action;
    private String description;
    private String ipAddress;
    private LocalDateTime date;
    private Long userId;       // apenas o ID do usuário
    private String username;   // opcional, se quiser
}