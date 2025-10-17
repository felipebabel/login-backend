package com.securityspring.infrastructure.adapters.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenVO {

    private String token;

    private String refreshToken;

    private Long expiresIn;

    private String role;

}
