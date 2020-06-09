package io.platformbuilders.shortenerurl.api.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortUrlResponse {

    private String key;
    
    private String shortUrl;
    
    private String originalUrl;
    
    private Long ttlInSeconds;
    
    private LocalDateTime createdAt;
    
    private Integer redirectHttpStatus;
}
