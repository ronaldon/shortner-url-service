package io.platformbuilders.shortenerurl.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.platformbuilders.shortenerurl.exception.UnprocessableEntityException;
import io.platformbuilders.shortenerurl.service.UrlShortnerService;
import io.platformbuilders.shortenerurl.service.entity.ShortUrlEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UrlShortnerServiceImpl implements UrlShortnerService {

    private static final int MAX_KEY_GENERATION_ATTEMPTS = 50;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Gson gson = new GsonBuilder().create();
    
    @Override
    public ShortUrlEntity create(ShortUrlEntity shortUrl) {
        UrlValidator urlValidator = new UrlValidator();
        String url = shortUrl.getOriginalUrl();
        
        if (shortUrl.getRedirectHttpStatus() != null 
            && !HttpStatus.valueOf(shortUrl.getRedirectHttpStatus()).is3xxRedirection()) {
            throw new UnprocessableEntityException("Invalid redirect http status " + shortUrl.getRedirectHttpStatus());
        }
        
        if (urlValidator.isValid(url)) {
            ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
            
            String generatedKey = generateKey(url);
            
            shortUrl.setKey(generatedKey);
            
            shortUrl.setShortUrl(shortUrl.getDomain() + "/" + generatedKey);    
            
            if (shortUrl.getRedirectHttpStatus() == null) {
                shortUrl.setRedirectHttpStatus(HttpStatus.MOVED_PERMANENTLY.value());
            }
            
            log.debug("URL short url: {}", shortUrl.getShortUrl());
            
            String json = gson.toJson(shortUrl);
            if (shortUrl.getTtlInSeconds() > 0) {
                opsForValue.set(generatedKey, json, shortUrl.getTtlInSeconds(), TimeUnit.SECONDS);
            } else {
                opsForValue.set(generatedKey, json);
            }
            
            return shortUrl;
        }
        
        throw new UnprocessableEntityException("Invalid URL: " + url);

    }

    @Override
    public Boolean remove(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public ShortUrlEntity getShortUrl(@PathVariable String id) {
        String json = redisTemplate.opsForValue().get(id);
        
        ShortUrlEntity entity = null;
        if (json != null) {
            entity = gson.fromJson(json, ShortUrlEntity.class);
            log.debug("URL Retrieved: {}", entity);            
        }
        
        return entity;
    }
    
    private String generateKey(String url) {
        
        int attempts = 0;
        String generatedKey = null;
        Boolean alreadyExists = null;
        do {
            
            generatedKey = Hashing.murmur3_32().hashString(
                url + System.nanoTime(), StandardCharsets.UTF_8).toString();
            
            alreadyExists = redisTemplate.opsForValue().get(generatedKey) != null;
            
        } while (alreadyExists && ++attempts < MAX_KEY_GENERATION_ATTEMPTS);
        
        if (alreadyExists) {
            throw new RuntimeException("Can't generate a unique short url hash!");
        }
        
        return generatedKey;
    }
}
