package com.securityspring.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDto {

    private String to;
    @Builder.Default
    private String subject = "Reset Your Password - Action Required";
    private String body;
    private String from;
}