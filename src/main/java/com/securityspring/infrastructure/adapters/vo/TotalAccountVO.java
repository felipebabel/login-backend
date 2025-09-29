package com.securityspring.infrastructure.adapters.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TotalAccountVO {

    private static final long serialVersionUID = 3L;

    private Long total;

    private Long totalActive;

    private Long totalInactive;

    private Long totalPending;

    private Long totalBlocked;

    private Long totalActiveSession;

}
