package com.securityspring.infrastructure.adapters.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAccountRequestDto {

    @NotBlank(message = "Username cannot be blank")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]{3,20}$",
            message = "Username can only contain letters, numbers, '.', '-' or '_' and must be 3-20 characters long"
    )
    private String user;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Name cannot be blank")
    @Pattern(
            regexp = "^[\\p{L}]+(?:[\\p{L}\\s'-]*[\\p{L}])?$",
            message = "Name can only contain letters, spaces, hyphens and apostrophes"
    )
    private String name;
}
