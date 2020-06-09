package io.platformbuilders.shortenerurl.converter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import io.platformbuilders.shortenerurl.api.dto.ShortUrlRequest;
import io.platformbuilders.shortenerurl.api.dto.ShortUrlResponse;
import io.platformbuilders.shortenerurl.service.entity.ShortUrlEntity;

@Component
public class ShorUrlConverter {
    
    public ShortUrlEntity convert(final ShortUrlRequest request) {
        
        return ShortUrlEntity.builder()
            .createdAt(LocalDateTime.now())
            .ttlInSeconds(request.getTtlInSeconds())
            .domain(request.getDomain())
            .redirectHttpStatus(request.getRedirectHttpStatus())
            .originalUrl(request.getLongUrl()).build();

    }
    
    public ShortUrlResponse convert(final ShortUrlEntity entity) {
        
        return ShortUrlResponse.builder()
            .key(entity.getKey())
            .originalUrl(entity.getOriginalUrl())
            .createdAt(entity.getCreatedAt())    
            .shortUrl(entity.getShortUrl())
            .ttlInSeconds(entity.getTtlInSeconds())
            .redirectHttpStatus(entity.getRedirectHttpStatus())
            .build();

    }

}
