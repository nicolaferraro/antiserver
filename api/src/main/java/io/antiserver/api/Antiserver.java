package io.antiserver.api;

import java.util.concurrent.CompletableFuture;

public interface Antiserver {

    <T> CompletableFuture<AntiserverResponse<T>> process(AntiserverRequest<T> request);

}
