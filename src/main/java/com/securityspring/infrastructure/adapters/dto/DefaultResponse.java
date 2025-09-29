package com.securityspring.infrastructure.adapters.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultResponse {

    public static final String ERROR = "Error";

    public static final String WARNING = "Warning";

    public static final String SUCCESS = "Success";

    @Builder.Default
    private String status = "SUCCESS";

    @Builder.Default
    private Date timestamp = new Date();

    private String message;

}
