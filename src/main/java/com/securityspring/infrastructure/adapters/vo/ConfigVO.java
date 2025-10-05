package com.securityspring.infrastructure.adapters.vo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigVO {

    private String key;
    private String value;
    private LocalDateTime updateDate;
}
