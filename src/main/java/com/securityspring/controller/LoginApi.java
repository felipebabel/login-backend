package com.securityspring.controller;

import com.securityspring.util.DefaultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
        ResponseEntity<DefaultResponse> login(@RequestParam("user") final String user,
                                              @RequestParam("password") final String password) throws BadRequestException;

        @Operation(summary = "Create Account", description = "Creates a new user account with encrypted password.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Account created successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "400", description = "User already exists")
        })
        @PostMapping("/create-account")
        ResponseEntity<DefaultResponse> createAccount(@RequestParam("user") final String user,
                                                      @RequestParam("password") final String password) throws BadRequestException;

        @Operation(summary = "Updated password", description = "Allows updating the password for an existing user.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Password updated successfully",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
                @ApiResponse(responseCode = "404", description = "User not found.")
        })
        @PutMapping("/forgot-password")
        ResponseEntity<DefaultResponse> forgotPassword(@RequestParam("user") final String user,
                                                      @RequestParam("password") final String password) throws BadRequestException;

}
