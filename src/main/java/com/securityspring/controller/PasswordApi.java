package com.securityspring.controller;

import com.securityspring.util.DefaultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public interface PasswordApi {

    @Operation(summary = "Encrypt a password", description = "Encrypts the provided password and returns the encrypted string.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password encrypted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/encrypt")
    public ResponseEntity<DefaultResponse> encryptPassword(final String password) throws BadRequestException;

    @Operation(summary = "Decrypt a password", description = "Decrypts the provided password and returns the original string.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password decrypted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/decrypt")
    public ResponseEntity<DefaultResponse> decryptPassword(final String password) throws Exception;

}
