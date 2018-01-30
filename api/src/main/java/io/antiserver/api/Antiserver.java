package io.antiserver.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Antiserver {

    <T> CompletableFuture<AntiserverResponse<T>> process(AntiserverRequest<T> request);

    CompletableFuture<Void> preload(List<AntiserverMavenDependency> dependencies);

}
