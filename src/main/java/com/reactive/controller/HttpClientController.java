package com.reactive.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/httpclient")
public class HttpClientController {
    private static final Log logger = LogFactory.getLog(HttpClientController.class);

    private final HttpClient httpClient;
    private final String bookStoreServiceHost;

    @Autowired
    public HttpClientController(@Value("${bookstore.service.host}") String bookStoreServiceHost) {
        this.bookStoreServiceHost = bookStoreServiceHost;
        this.httpClient = HttpClient.newBuilder().executor(Executors.newSingleThreadExecutor()).build();
    }

    @GetMapping(value = "/sync")
    public String sync(@RequestParam long delay) throws InterruptedException {
        logger.info("sync " + delay + " - Executed in thread: " + Thread.currentThread().getName());
        try {
            return sendRequestSync(delay);
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping(value = "/completable-future")
    public CompletableFuture<String> completableFuture(@RequestParam long delay) {
        logger.info("completable-future-java-client - Executed in thread: " + Thread.currentThread().getName());
        return sendRequestAsync(delay).thenApply(x -> {
            logger.info("completable JC - Executed in thread: " + Thread.currentThread().getName());
            return "completable-future-java-client: " + x;
        });
    }

    @GetMapping(value = "/webflux")
    public Mono<String> webflux(@RequestParam long delay) {
        logger.info("webflux-java-http-client - Executed in thread: " + Thread.currentThread().getName());
        CompletableFuture<String> stringCompletableFuture = sendRequestAsync(delay).thenApply(x -> {
            logger.info("webflux JCS - Executed in thread: " + Thread.currentThread().getName());
            return "webflux-java-http-client: " + x;
        });
        return Mono.fromFuture(stringCompletableFuture);
    }

    private String sendRequestSync(long delay) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format("%s/book/?delay=%d", bookStoreServiceHost, delay)))
            .GET()
            .build();
        var result = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return result.body();
    }

    private CompletableFuture<String> sendRequestAsync(long delay) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format("%s/book/?delay=%d", bookStoreServiceHost, delay)))
            .GET()
            .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body);
    }
}