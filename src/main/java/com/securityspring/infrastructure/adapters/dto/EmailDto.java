package com.securityspring.infrastructure.adapters.dto;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailDto {

    private String to;
    @Builder.Default
    private String subject = "Reset Your Password - Action Required";
    private String body;
    private String from;
}