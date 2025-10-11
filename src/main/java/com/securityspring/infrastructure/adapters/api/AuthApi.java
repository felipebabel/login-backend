package com.securityspring.infrastructure.adapters.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.securityspring.infrastructure.adapters.dto.RefreshTokenRequestDto;
import com.securityspring.infrastructure.adapters.vo.TokenVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthApi {

    @Operation(
            summary = "Refresh Access Token",
            description = "Generates a new access token using a valid refresh token. " +
                    "If the provided refresh token is expired or invalid, an HTTP 401 is returned."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Access token successfully refreshed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenVO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error"
            )
    })
    ResponseEntity<TokenVO> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Object containing the refresh token to be renewed",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RefreshTokenRequestDto.class)
                    )
            )
            @RequestBody RefreshTokenRequestDto request
    );
}
