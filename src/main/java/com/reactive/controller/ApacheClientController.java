package com.reactive.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/apacheclient")
public class ApacheClientController {
    private static final Log logger = LogFactory.getLog(ApacheClientController.class);

    private final String bookStoreServiceHost;
    private final CloseableHttpAsyncClient apacheClient;

    @Autowired
    public ApacheClientController(@Value("${bookstore.service.host}") String bookStoreServiceHost) {
        this.bookStoreServiceHost = bookStoreServiceHost;
        this.apacheClient = HttpAsyncClients.custom().setMaxConnPerRoute(2000).setMaxConnTotal(2000).build();
        this.apacheClient.start();
    }

    @GetMapping(value = "/sync")
    public String sync(@RequestParam long delay) {
        logger.info("sync " + delay + " - Executed in thread: " + Thread.currentThread().getName());
        return sendRequest(delay).thenApply(x -> {
            logger.info("sync - Executed in thread: " + Thread.currentThread().getName());
            return "sync: " + x;
        }).join();
    }

    @GetMapping(value = "/completable-future")
    public CompletableFuture<String> completableFuture(@RequestParam long delay) {
        logger.info("completable-future-apache-client - Executed in thread: " + Thread.currentThread().getName());
        return sendRequest(delay).thenApply(x -> {
            logger.info("completable-future-apache-client - Executed in thread: " + Thread.currentThread().getName());
            return "completable-future-apache-client: " + x;
        });
    }

    @GetMapping(value = "/webflux")
    public Mono<String> webflux(@RequestParam long delay) {
        return Mono.fromCompletionStage(sendRequest(delay).thenApply(x -> {
            logger.info("webflux-apache-client - Executed in thread: " + Thread.currentThread().getName());
            return "webflux-apache-client: " + x;
        }));
    }

    private CompletableFuture<String> sendRequest(long delay) {
        CompletableFuture<org.apache.http.HttpResponse> cf = new CompletableFuture<>();
        FutureCallback<org.apache.http.HttpResponse> callback = new HttpResponseCallback(cf);
        HttpUriRequest request = new HttpGet(bookStoreServiceHost + "/book/?delay=" + delay);
        apacheClient.execute(request, callback);
        return cf.thenApply(response -> {
            try {
                return EntityUtils.toString(response.getEntity());
            } catch (ParseException | IOException e) {
                return e.toString();
            }
        }).exceptionally(Throwable::toString);
    }
}