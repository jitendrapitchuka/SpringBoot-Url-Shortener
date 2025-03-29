package com.jitendra.urlshortener.domain.services;

import com.jitendra.urlshortener.domain.entities.ShortUrl;
import com.jitendra.urlshortener.domain.entities.User;
import com.jitendra.urlshortener.domain.models.ShortUrlDto;
import com.jitendra.urlshortener.domain.models.UserDto;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public ShortUrlDto toShortUrlDto(ShortUrl shortUrl){
        UserDto userDto=null;

        if(shortUrl.getCreatedAt()!=null){
            userDto=toUserDto(shortUrl.getCreatedBy());
        }

        return new ShortUrlDto(
                shortUrl.getId(),
                shortUrl.getShortKey(),
                shortUrl.getOriginalUrl(),
                shortUrl.getIsPrivate(),
                shortUrl.getExpiresAt(),
                userDto,
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt()
        );
    }

    public UserDto toUserDto(User user){
        return new UserDto(user.getId(), user.getName());
    }
}
