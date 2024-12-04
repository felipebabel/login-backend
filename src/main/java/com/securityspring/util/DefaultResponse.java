package com.securityspring.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DefaultResponse {

    @Builder.Default
    private String status = "SUCCESS";

    @Builder.Default
    private Date timestamp = new Date();

    private String message;

}
