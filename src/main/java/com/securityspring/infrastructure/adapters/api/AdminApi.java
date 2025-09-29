package com.securityspring.infrastructure.adapters.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.securityspring.domain.exception.BaseException;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface AdminApi {

    @Operation(summary = "Get Pending Accounts",
            description = "Returns a paginated list of users with pending account status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of pending accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getPendingAccount(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction", example = "asc") @RequestParam(defaultValue = "asc") String direction
    ) throws BadRequestException;

    @Operation(summary = "Get Pending Accounts",
            description = "Returns a paginated list of users with pending account status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of pending accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getLogs(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction", example = "asc") @RequestParam(defaultValue = "asc") String direction,
            @RequestParam Long userIdentifier,
            @RequestParam(required = false) String username,
            @RequestParam String action
    ) throws BadRequestException;

    @Operation(summary = "Get Pending Accounts",
            description = "Returns a paginated list of users with pending account status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of pending accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getUsers(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction", example = "asc") @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) Long userIdentifier,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String name
    ) throws BadRequestException;

    @Operation(summary = "Get Active Accounts",
            description = "Returns a paginated list of users with active account status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of active accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getActiveAccount(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction", example = "asc") @RequestParam(defaultValue = "asc") String direction
    ) throws BaseException;

    @Operation(summary = "Get Blocked Accounts",
            description = "Returns a paginated list of users with blocked account status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of blocked accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getBlockedAccount(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction", example = "asc") @RequestParam(defaultValue = "asc") String direction
    ) throws BadRequestException;

    @Operation(summary = "Get Blocked Accounts",
            description = "Returns a paginated list of users with blocked account status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of blocked accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> updateUserRole(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam final Long userIdentifier,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam final String role,
                        @Parameter(description = "Number of records per page", example = "10") @RequestParam final Long userRequired
    ) throws BadRequestException;

    @Operation(summary = "Get Active Sessions Accounts",
            description = "Returns a paginated list of users with active sessions account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of active sessions accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getActiveSession(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction", example = "asc") @RequestParam(defaultValue = "asc") String direction
    ) throws BadRequestException;

    @Operation(summary = "Get Inactive Accounts",
            description = "Returns a paginated list of users with inactive account status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of inactive accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getInactiveAccount(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction", example = "asc") @RequestParam(defaultValue = "asc") String direction
    ) throws BadRequestException;

    @Operation(summary = "Get Total Accounts",
            description = "Returns the total number of user accounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total number of accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    ResponseEntity<Object> getTotalAccount() throws BadRequestException;

    @Operation(summary = "Inactive User Account",
            description = "Inactivates a user account by user identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account inactivated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier")
    })
    ResponseEntity<Object> inactiveUser(
            @Parameter(description = "User identifier to inactivate", example = "123") @RequestParam("user") Long user
    ) throws BadRequestException;

    @Operation(summary = "Inactive User Account", //TODO FIX
            description = "Inactivates a user account by user identifier.") //TODO FIX
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account inactivated successfully", //TODO FIX
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier")
    })
    ResponseEntity<Object> forcePasswordChange(
            @Parameter(description = "User identifier to inactivate", example = "123") @RequestParam("user") Long user //TODO FIX
    ) throws BadRequestException;

    @Operation(summary = "Get Total Accounts", //todo fix
            description = "Returns the total number of user accounts.") //todo fix
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total number of accounts retrieved successfully", //todo fix
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    ResponseEntity<Object> getUserByUsername(@RequestParam(value = "username", required = false) String username,
                                             @RequestParam(value = "name", required = false) String name) throws BadRequestException; //todo fix

    @Operation(summary = "Block User Account",
            description = "Blocks a user account by user identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account blocked successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier")
    })
    ResponseEntity<Object> blockUser(
            @Parameter(description = "User identifier to block", example = "123") @RequestParam("user") Long user
    ) throws BadRequestException;

    @Operation(summary = "Activate User Account",
            description = "Activates a user account by user identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account activated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier")
    })
    ResponseEntity<Object> activeUser(
            @Parameter(description = "User identifier to activate", example = "123") @RequestParam("user") Long user
    ) throws BadRequestException;

    @Operation(summary = "Delete User Account",
            description = "Deletes a user account by user identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier")
    })
    ResponseEntity<Object> deleteUser(
            @Parameter(description = "User identifier to delete", example = "123") @RequestParam("user") Long user
    ) throws BadRequestException;
}
