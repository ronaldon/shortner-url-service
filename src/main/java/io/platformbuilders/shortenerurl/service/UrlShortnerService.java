package io.platformbuilders.shortenerurl.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.platformbuilders.shortenerurl.service.entity.ShortUrlEntity;

public interface UrlShortnerService {

    ShortUrlEntity create(@RequestBody ShortUrlEntity shortUrl);
    
    ShortUrlEntity getShortUrl(@PathVariable String id);
    
    Boolean remove(String key);
}
