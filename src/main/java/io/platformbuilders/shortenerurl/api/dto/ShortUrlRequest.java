package io.platformbuilders.shortenerurl.api.dto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Valid
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortUrlRequest {

    @NotBlank(message = "Please provide a 'longUrl' value attribute")
    private String longUrl;
    
    @NotBlank(message = "Please provide a 'domain' value attribute")
    private String domain;
    
    @NotNull(message = "Please provide a 'ttlInSeconds' value attribute")
    @Min(value = 0, message = "'ttlInSeconds' attribute must be igual or greater than zero")
    private Long ttlInSeconds;
    
    private Integer redirectHttpStatus;
}
