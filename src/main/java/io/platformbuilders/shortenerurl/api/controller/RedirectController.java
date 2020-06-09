package io.platformbuilders.shortenerurl.api.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.platformbuilders.shortenerurl.service.UrlShortnerService;
import io.platformbuilders.shortenerurl.service.entity.ShortUrlEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/redirect")
public class RedirectController {

    @Autowired
    UrlShortnerService urlShortnerService;

    @GetMapping(path = "/{key}")
    public ResponseEntity<String> redirect(@PathVariable String key) {

        log.debug("Request short url key: {}", key);
        
        ShortUrlEntity entity = urlShortnerService.getShortUrl(key);

        if (entity == null) {
            log.warn("URL key not found: {}", key);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String url = entity.getOriginalUrl();

        if (!url.startsWith("http")) {
            url = "http://" + url;
        }

        log.debug("Redirecting to URL: {}", url);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.valueOf(entity.getRedirectHttpStatus()));

    }

}
