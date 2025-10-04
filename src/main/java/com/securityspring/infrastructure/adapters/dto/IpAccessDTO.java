package com.securityspring.infrastructure.adapters.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IpAccessDTO {

    private String ip;
    private Long totalAccesses;

}
