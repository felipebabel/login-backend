package com.securityspring.infrastructure.adapters.api;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import com.securityspring.infrastructure.adapters.dto.UpdateAccountRequestDto;
import com.securityspring.infrastructure.config.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Operation(summary = "Delete User Account",
            description = "Deletes a user account by identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier")
    })
    ResponseEntity<Object> deleteUser(
            @Parameter(description = "User identifier to delete", example = "123") @RequestParam("user") Long user,
            final HttpServletRequest httpServletRequest
    ) throws BadRequestException;

    @Operation(
            summary = "Get My Logs",
            description = "Returns a paginated list of logs for the authenticated user. Only accessible by users with USER role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logs retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    ResponseEntity<Object> getMyLogs(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(defaultValue = "description") String sortBy,
                                     @RequestParam(defaultValue = "asc") String direction,
                                     @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "Get Own User Data",
            description = "Retrieves the data of the currently authenticated user. " +
                    "No parameters are required; the endpoint uses the authenticated user's credentials."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - user is not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have the required role")
    })
    ResponseEntity<Object> getMyUserData(@AuthenticationPrincipal CustomUserDetails userDetails);

}
