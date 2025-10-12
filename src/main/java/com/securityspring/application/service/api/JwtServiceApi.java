package com.securityspring.application.service.api;

import com.securityspring.infrastructure.adapters.dto.RefreshTokenRequestDto;
import com.securityspring.infrastructure.adapters.vo.TokenVO;
import com.securityspring.infrastructure.adapters.vo.UserVO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtServiceApi {

    TokenVO generateToken(final UserVO userVO);

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractUsername(String token);
    String extractClaimByName(String token, String claimName);

    ResponseEntity<TokenVO> refreshToken(final RefreshTokenRequestDto requestDto, final UserVO userVO);
    }
