package com.reactive.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class BookStoreController {

    @GetMapping("/book")
    public Mono<String> getBookWithDelay(@RequestParam long delay) {
        return Mono.just("Reactive Programming With Java").delayElement(Duration.ofMillis(delay));
    }
}
