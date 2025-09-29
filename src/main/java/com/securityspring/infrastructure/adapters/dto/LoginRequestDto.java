package com.securityspring.infrastructure.adapters.dto;

import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequestDto {

    @NotBlank(message = "Username cannot be blank")
    private String user;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
