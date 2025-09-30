package com.securityspring.infrastructure.adapters.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import com.securityspring.infrastructure.adapters.dto.CreateAccountRequestDto;
import com.securityspring.infrastructure.adapters.dto.LoginRequestDto;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
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
        ResponseEntity<Object> login(@Valid @RequestBody final LoginRequestDto loginRequest,
                                     final HttpServletRequest httpServletRequest) throws BadRequestException ;

        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists")
        })
        ResponseEntity<Object> createAccount(@Valid @RequestBody final CreateAccountRequestDto createAccount,
                                             final HttpServletRequest httpServletRequest) throws BadRequestException, MessagingException, UnsupportedEncodingException;

        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists")
        })
        ResponseEntity<Object> updateAccount(@RequestParam("user") final Long userIdentifier,
                                             @Valid @RequestBody final UpdateAccountRequestDto createAccount,
                                             final HttpServletRequest httpServletRequest) throws BadRequestException, MessagingException, UnsupportedEncodingException;

        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.") //TODO FIX
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully", //TODO FIX
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists") //TODO FIX
        })
        ResponseEntity<Object> logout(@RequestParam("user") Long userIdentifier,
                                      final HttpServletRequest httpServletRequest) throws BadRequestException;

        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.") //TODO FIX
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully", //TODO FIX
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists") //TODO FIX
        })
        ResponseEntity<Object> sendEmail(@RequestParam("email") String email,
                                         final HttpServletRequest httpServletRequest);

        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.") //TODO FIX
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully", //TODO FIX
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists") //TODO FIX
        })
        ResponseEntity<Object> validateCode(@RequestParam("code") final String code,
                                            @RequestParam("email") final String email,
                                            final HttpServletRequest httpServletRequest) throws BadRequestException;


        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.") //TODO FIX
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully", //TODO FIX
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists") //TODO FIX
        })
        ResponseEntity<Object> resetPassword(@RequestParam("newPassword") final String newPassword,
                                             @RequestParam(value = "email", required = false) final String email,
                                             @RequestParam(value = "user", required = false) final String user,
                                             final HttpServletRequest httpServletRequest) throws BadRequestException;

}
