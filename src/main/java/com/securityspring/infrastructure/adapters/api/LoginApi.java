package com.securityspring.infrastructure.adapters.api;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        @PostMapping("/login")
        ResponseEntity<Object> login(@Valid @RequestBody final LoginRequestDto loginRequest) throws BadRequestException ;

        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists")
        })
        ResponseEntity<Object> createAccount(@Valid @RequestBody final CreateAccountRequestDto createAccount) throws BadRequestException, MessagingException, UnsupportedEncodingException;

        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.") //TODO FIX
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully", //TODO FIX
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists") //TODO FIX
        })
        ResponseEntity<Object> logout(@RequestParam("user") Long userIdentifier) throws BadRequestException;

        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.") //TODO FIX
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully", //TODO FIX
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists") //TODO FIX
        })
        ResponseEntity<Object> sendEmail(@RequestParam("email") String email) throws BadRequestException;

        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.") //TODO FIX
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully", //TODO FIX
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists") //TODO FIX
        })
        ResponseEntity<Object> validateCode(@RequestParam("code") final String code,
                                            @RequestParam("email") final String email) throws BadRequestException;


        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.") //TODO FIX
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully", //TODO FIX
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists") //TODO FIX
        })
        ResponseEntity<Object> resetPassword(@RequestParam("newPassword") final String newPassword,
                                            @RequestParam("email") final String email) throws BadRequestException;

}
