package com.reactive.controller;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

import java.util.concurrent.CompletableFuture;

class HttpResponseCallback implements FutureCallback<HttpResponse> {

    private final CompletableFuture<HttpResponse> completableFuture;

    HttpResponseCallback(CompletableFuture<HttpResponse> completableFuture) {
        this.completableFuture = completableFuture;
    }

    @Override
    public void failed(Exception ex) {
        completableFuture.completeExceptionally(ex);
    }

    @Override
    public void completed(HttpResponse result) {
        completableFuture.complete(result);
    }

    @Override
    public void cancelled() {
        completableFuture.completeExceptionally(new Exception("Cancelled by http async client"));
    }
}
