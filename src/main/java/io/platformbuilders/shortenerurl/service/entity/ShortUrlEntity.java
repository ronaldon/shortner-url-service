package io.platformbuilders.shortenerurl.service.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortUrlEntity {

    private String key;
    
    private String originalUrl;
    
    private String shortUrl;
    
    private String domain;
    
    private Long ttlInSeconds;

    private LocalDateTime createdAt;
    
    private Integer redirectHttpStatus;
    
}
