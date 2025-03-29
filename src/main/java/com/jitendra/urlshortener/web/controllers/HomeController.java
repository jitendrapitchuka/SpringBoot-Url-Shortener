package com.jitendra.urlshortener.web.controllers;

import com.jitendra.urlshortener.domain.models.ShortUrlDto;
import org.springframework.ui.Model;
import com.jitendra.urlshortener.domain.services.ShortUrlService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ShortUrlService shortUrlService;

    public HomeController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @GetMapping("/")
    public String home(Model model){
    List<ShortUrlDto> shortUrls=shortUrlService.findAllPublicShortUrls();
    model.addAttribute("shortUrls", shortUrls);
    model.addAttribute("baseUrl","http://localhost:8080");
        return "index";
    }

}
