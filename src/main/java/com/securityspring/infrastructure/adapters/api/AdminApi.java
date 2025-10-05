package com.securityspring.infrastructure.adapters.api;

import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.securityspring.domain.exception.BadRequestException;
import com.securityspring.domain.exception.BaseException;
import com.securityspring.infrastructure.adapters.dto.DefaultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface AdminApi {

    @Operation(summary = "Get Pending Accounts",
            description = "Returns a paginated list of users whose accounts are pending approval.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getPendingAccount(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction
    ) throws BadRequestException;

    @Operation(summary = "Get User Logs",
            description = "Returns a paginated list of user action logs filtered by user and action type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logs retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getLogs(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Identifier of the user") @RequestParam Long userIdentifier,
            @Parameter(description = "Username filter (optional)") @RequestParam(required = false) String username,
            @Parameter(description = "Action type to filter logs") @RequestParam String action
    ) throws BadRequestException;

    @Operation(summary = "Get Users",
            description = "Returns a paginated list of users with optional filtering by identifier, username, or name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getUsers(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) Long userIdentifier,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String name
    ) throws BadRequestException;

    @Operation(summary = "Get Active Accounts",
            description = "Returns a paginated list of users whose accounts are active.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getActiveAccount(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction
    ) throws BaseException;

    @Operation(summary = "Get Blocked Accounts",
            description = "Returns a paginated list of users whose accounts are blocked.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blocked accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getBlockedAccount(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction
    ) throws BadRequestException;

    @Operation(summary = "Update User Role",
            description = "Updates the role of a user specified by the user identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> updateUserRole(
            @Parameter(description = "Identifier of the user to update") @RequestParam final Long userIdentifier,
            @Parameter(description = "Role to assign to the user") @RequestParam final String role,
            @Parameter(description = "Identifier of the user performing the change") @RequestParam final Long userRequired,
            final HttpServletRequest httpServletRequest
    ) throws BadRequestException;

    @Operation(summary = "Get Active Sessions",
            description = "Returns a paginated list of users with active sessions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active sessions retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getActiveSession(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction
    ) throws BadRequestException;

    @Operation(summary = "Get Login Attempts",
            description = "Returns a list of login attempts by users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login attempts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getLoginAttempts() throws BadRequestException;

    @Operation(summary = "Get New Accounts Per Month",
            description = "Returns the count of newly created accounts per month.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New accounts count retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getNewAccountMonth() throws BadRequestException;

    @Operation(summary = "Get Inactive Accounts",
            description = "Returns a paginated list of users whose accounts are inactive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inactive accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    ResponseEntity<Object> getInactiveAccount(
            @Parameter(description = "Page number, starting from 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "creationUserDate") @RequestParam(defaultValue = "creationUserDate") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction
    ) throws BadRequestException;

    @Operation(summary = "Get Total Accounts",
            description = "Returns the total number of user accounts in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    ResponseEntity<Object> getTotalAccount() throws BadRequestException;

    @Operation(summary = "Inactive User Account",
            description = "Inactivates a user account by its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account inactivated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier")
    })
    ResponseEntity<Object> inactiveUser(
            @Parameter(description = "User identifier to inactivate", example = "123") @RequestParam("user") Long user,
            final HttpServletRequest httpServletRequest
    ) throws BadRequestException;

    @Operation(summary = "Force Password Change",
            description = "Forces a password change for a user by identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password change forced successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier")
    })
    ResponseEntity<Object> forcePasswordChange(
            @Parameter(description = "User identifier to force password change", example = "123") @RequestParam("user") Long user,
            final HttpServletRequest httpServletRequest
    ) throws BadRequestException;

    @Operation(summary = "Get User By Username",
            description = "Retrieves users filtered by optional username and name parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    ResponseEntity<Object> getUserByUsername(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "name", required = false) String name
    ) throws BadRequestException;

    @Operation(summary = "Block User Account",
            description = "Blocks a user account by identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account blocked successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier")
    })
    ResponseEntity<Object> blockUser(
            @Parameter(description = "User identifier to block", example = "123") @RequestParam("user") Long user,
            final HttpServletRequest httpServletRequest
    ) throws BadRequestException;

    @Operation(summary = "Activate User Account",
            description = "Activates a user account by identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account activated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user identifier")
    })
    ResponseEntity<Object> activeUser(
            @Parameter(description = "User identifier to activate", example = "123") @RequestParam("user") Long user,
            final HttpServletRequest httpServletRequest
    ) throws BadRequestException;

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
}
