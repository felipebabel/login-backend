package com.securityspring.infrastructure.adapters.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewUsersPerMonthDTO {

    private Integer year;
    private Integer month;
    private Long totalUsers;

}
