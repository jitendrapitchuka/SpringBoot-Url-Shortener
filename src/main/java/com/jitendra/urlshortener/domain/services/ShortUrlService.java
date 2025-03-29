package com.jitendra.urlshortener.domain.services;

import com.jitendra.urlshortener.domain.models.ShortUrlDto;
import com.jitendra.urlshortener.domain.repositories.ShortUrlRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShortUrlService {

    public final ShortUrlRepository shortUrlRepository;

    public ShortUrlService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    public List<ShortUrlDto> findAllPublicShortUrls() {
        return shortUrlRepository.findPublicShortUrls().stream().map(a->new EntityMapper().toShortUrlDto(a)).toList();
    }

}
