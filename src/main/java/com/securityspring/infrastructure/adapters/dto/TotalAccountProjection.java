package com.securityspring.infrastructure.adapters.dto;

public interface TotalAccountProjection {
    Long getTotal();
    Long getTotalActive();
    Long getTotalInactive();
    Long getTotalPending();
    Long getTotalBlocked();
    Long getTotalActiveSession();
}
