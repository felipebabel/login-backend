package com.securityspring.infrastructure.adapters.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.ResetPasswordDto;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface LoginApi {

        @Operation(summary = "Login", description = "Validates user credentials and logs in the user.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Login successful",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid password"),
                @ApiResponse(responseCode = "404", description = "User not found")
        })
        ResponseEntity<Object> login(@Valid @RequestBody final LoginRequestDto loginRequest,
                                     final HttpServletRequest httpServletRequest) throws BadRequestException, InterruptedException;

        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password and sends confirmation email.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists")
        })
        ResponseEntity<Object> createAccount(@Valid @RequestBody final CreateAccountRequestDto createAccount,
                                             final HttpServletRequest httpServletRequest) throws BadRequestException, MessagingException, UnsupportedEncodingException;

        @Operation(summary = "Send Email", description = "Sends a confirmation or reset email to the specified address.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Email sent successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "Failed to send email")
        })
        ResponseEntity<Object> sendEmail(@RequestParam("email") String email,
                                         @RequestParam(value = "lang", required = false, defaultValue = "en") final String lang,
                                         final HttpServletRequest httpServletRequest) throws MessagingException;

        @Operation(summary = "Validate Code", description = "Validates a confirmation or password reset code sent via email.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Code validated successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid or expired code")
        })
        ResponseEntity<Object> validateCode(@RequestParam("code") final String code,
                                            @RequestParam("email") final String email,
                                            final HttpServletRequest httpServletRequest) throws BadRequestException;

        @Operation(summary = "Reset Password", description = "Resets the user password using email or user identifier.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Password reset successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "Failed to reset password due to invalid data")
        })
        ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordDto dto,
                                             final HttpServletRequest httpServletRequest) throws BadRequestException;

}
