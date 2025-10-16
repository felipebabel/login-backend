package com.securityspring.infrastructure.adapters.api;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface AccountApi {

    @Operation(summary = "Update Account", description = "Updates user account information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Update failed due to invalid data or user not found")
    })
    ResponseEntity<Object> updateAccount(@RequestParam("user") final Long userIdentifier,
                                         @Valid @RequestBody final UpdateAccountRequestDto createAccount,
                                         final HttpServletRequest httpServletRequest) throws BadRequestException, MessagingException, UnsupportedEncodingException;

    @Operation(summary = "Logout", description = "Logs out the specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Logout failed, user not found")
    })
    ResponseEntity<Object> logout(@RequestParam("user") Long userIdentifier,
                                  final HttpServletRequest httpServletRequest) throws BadRequestException;

}
