package com.jitendra.urlshortener.domain.services;

import com.jitendra.urlshortener.ApplicationProperties;
import com.jitendra.urlshortener.domain.entities.ShortUrl;
import com.jitendra.urlshortener.domain.models.CreateShortUrlCmd;
import com.jitendra.urlshortener.domain.models.ShortUrlDto;
import com.jitendra.urlshortener.domain.repositories.ShortUrlRepository;
import com.jitendra.urlshortener.web.controllers.HomeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ShortUrlService {

    private final Logger logger= LoggerFactory.getLogger(ShortUrlService.class);
    public final ShortUrlRepository shortUrlRepository;
    private final ApplicationProperties properties;
    private final EntityMapper entityMapper;

    public ShortUrlService(ShortUrlRepository shortUrlRepository, ApplicationProperties properties, EntityMapper entityMapper) {
        this.shortUrlRepository = shortUrlRepository;
        this.properties = properties;
        this.entityMapper = entityMapper;
    }

    public List<ShortUrlDto> findAllPublicShortUrls() {
        return shortUrlRepository.findPublicShortUrls().stream().map(a->new EntityMapper().toShortUrlDto(a)).toList();
    }

    @Transactional
    public ShortUrlDto createShortUrl(CreateShortUrlCmd cmd) {
        if(properties.validateOriginalUrl()){
            boolean urlExists = UrlExistenceValidator.isUrlExists(cmd.originalUrl());
            logger.info("Found url");
            if(!urlExists){
                throw new RuntimeException("Invalid Original URL" + cmd.originalUrl());
            }
        }

        var shortKey=generateUniqueShortKey();
        var shortUrl=new ShortUrl();
        shortUrl.setOriginalUrl(cmd.originalUrl());
        shortUrl.setShortKey(shortKey);
        shortUrl.setCreatedBy(null);
        shortUrl.setIsPrivate(false);
        shortUrl.setClickCount(0L);
        shortUrl.setExpiresAt(Instant.now().plus(properties.defaultExpiryInDays(), ChronoUnit.DAYS));
        shortUrl.setCreatedAt(Instant.now());
        shortUrlRepository.save(shortUrl);

        return entityMapper.toShortUrlDto(shortUrl);
    }

    private String generateUniqueShortKey() {
        String shortKey;
        do {
            shortKey = generateRandomShortKey();
        } while (shortUrlRepository.existsByShortKey(shortKey));
        return shortKey;
    }
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_KEY_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();
    public static String generateRandomShortKey() {
        StringBuilder sb = new StringBuilder(SHORT_KEY_LENGTH);
        for (int i = 0; i < SHORT_KEY_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

}
