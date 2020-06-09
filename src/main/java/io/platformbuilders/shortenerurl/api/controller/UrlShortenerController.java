package io.platformbuilders.shortenerurl.api.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.platformbuilders.shortenerurl.api.dto.ShortUrlRequest;
import io.platformbuilders.shortenerurl.api.dto.ShortUrlResponse;
import io.platformbuilders.shortenerurl.converter.ShorUrlConverter;
import io.platformbuilders.shortenerurl.exception.NotFoundException;
import io.platformbuilders.shortenerurl.service.UrlShortnerService;
import io.platformbuilders.shortenerurl.service.entity.ShortUrlEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * URL Shortener Resource
 *
 * @author ronaldo.ronie@platformbuilders.io
 * 
 */
@Slf4j
@RestController
@RequestMapping(path = "/url")
public class UrlShortenerController {

    @Autowired
    UrlShortnerService urlShortnerService;

    @Autowired
    ShorUrlConverter converter;
    
    @PostMapping
    public ResponseEntity<Object> create(
        @Valid @RequestBody(required = true) 
        final ShortUrlRequest request) throws URISyntaxException {
        
        log.debug("Creating short url {}", request);
        
        ShortUrlEntity entity = urlShortnerService.create(converter.convert(request));
        
        ShortUrlResponse response = converter.convert(entity);
        
        URI location = new URI(response.getShortUrl());
        return ResponseEntity.created(location).header("hash", response.getKey()).build();       
    }
    
    @GetMapping("/{key}")
    public ResponseEntity<ShortUrlEntity> get(@PathVariable final String key) {
        
        log.debug("Get short url entity {}", key);
        
        ShortUrlEntity shortUrl = urlShortnerService.getShortUrl(key);
        if (shortUrl == null) {
            throw new NotFoundException("Url not found!");
        }
        
        return ResponseEntity.ok(shortUrl);
    }
    
    @DeleteMapping("/{key}")
    public ResponseEntity<String> remove(@PathVariable final String key) {
        
        log.debug("Removing short url {}", key);
        
        Boolean removed = urlShortnerService.remove(key);
        if (!removed) {
            throw new NotFoundException("Url not found!");
        }
        
        return ResponseEntity.ok().build();
    }
    
}
