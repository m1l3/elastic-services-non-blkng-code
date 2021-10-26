package com.reactive.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webclient")
public class WebClientController {
    private static final Log logger = LogFactory.getLog(WebClientController.class);

    private final WebClient webClient;

    @Autowired
    public WebClientController(@Value("${bookstore.service.host}") String bookStoreServiceHost) {
        this.webClient = WebClient.builder().baseUrl(bookStoreServiceHost).build();
    }

    @GetMapping(value = "/webflux")
    public Mono<String> webflux(@RequestParam long delay) {
        logger.info("webflux-webclient - Executed in thread: " + Thread.currentThread().getName());
        return webClient.get().uri("/book/?delay={delay}", delay).retrieve().bodyToMono(String.class).map(x -> {
            logger.info("webflux-webclient - Executed in thread: " + Thread.currentThread().getName());
            return "webflux-webclient: " + x;
        });
    }
}